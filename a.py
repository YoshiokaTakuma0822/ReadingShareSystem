from fastapi import FastAPI, HTTPException
from pathlib import Path
import shutil
import subprocess
import requests
import zipfile
import io
import time

app = FastAPI(
    docs_url=None,
    redoc_url=None,
    openapi_url=None
)

# — 設定値 — 
OWNER     = "YoshiokaTakuma0822"
REPO      = "ReadingShareSystem"
BRANCH    = "dev4"
ARCHIVE_URL = f"https://codeload.github.com/{OWNER}/{REPO}/zip/{BRANCH}"
WORK_DIR  = Path("/home/ec2-user/myapp")      # ソース作業ディレクトリ
EXTRACT_DIR = WORK_DIR / "src"      # ZIP を展開する先
COMPOSE_DIR = EXTRACT_DIR / f"{REPO}-{BRANCH}"

# Add rate limiting utilities
last_deploy_time = 0.0
last_reset_time = 0.0

def run_cmd(cmd: str, cwd: Path = None) -> str:
    result = subprocess.run(
        cmd, shell=True, cwd=cwd,
        capture_output=True, text=True
    )
    if result.returncode != 0:
        raise RuntimeError(result.stderr or result.stdout)
    return result.stdout

def fetch_and_extract():
    """
    1) ARCHIVE_URL から ZIP をダウンロード  
    2) EXTRACT_DIR を丸ごと削除して再作成  
    3) ZIP をメモリ上で解凍
    """
    # ダウンロード
    resp = requests.get(ARCHIVE_URL, stream=True, timeout=30)
    resp.raise_for_status()

    # フォルダをクリア
    if EXTRACT_DIR.exists():
        shutil.rmtree(EXTRACT_DIR)
    EXTRACT_DIR.mkdir(parents=True, exist_ok=True)

    # ZIP 解凍
    with zipfile.ZipFile(io.BytesIO(resp.content)) as z:
        z.extractall(EXTRACT_DIR)

@app.get("/b0e873fd-af04-4b45-b0cc-95a990f1077d/deploy")
async def deploy():
    """
    - ZIP をダウンロード → 展開  
    - COMPOSE_DIR 以下で docker compose up -d
    """
    global last_deploy_time

    # Rate limiting: 5秒間隔
    if time.time() - last_deploy_time < 11:
        raise HTTPException(status_code=429, detail="Too many requests")

    try:
        fetch_and_extract()
        run_cmd("docker compose up -d", cwd=COMPOSE_DIR)
        last_deploy_time = time.time()
        return {"status": "deployed via HTTP archive"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/b0e873fd-af04-4b45-b0cc-95a990f1077d/reset")
async def reset():
    """
    - COMPOSE_DIR 以下で docker compose down  
    - 再度 up -d
    """
    global last_reset_time

    # Rate limiting: 5秒間隔
    if time.time() - last_reset_time < 11:
        raise HTTPException(status_code=429, detail="Too many requests")

    try:
        run_cmd("docker compose down -v", cwd=COMPOSE_DIR)
        run_cmd("docker compose up -d",   cwd=COMPOSE_DIR)
        last_reset_time = time.time()
        return {"status": "reset and deployed"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn  # type: ignore
    # uvicorn.run("main:app", host="0.0.0.0", port=8080, reload=True)
    uvicorn.run(app, host="0.0.0.0", port=8080)

# >>> import uuid
# >>> uuid.uuid4()
