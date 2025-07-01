"use client";
import React, { useState, useEffect } from "react";
import ReadingProgressModal from "./ReadingProgressModal";
import { readingStateApi } from '../../lib/readingStateApi';
import { authStorage } from '../../lib/authUtils';

const maxPage = 300;

interface ReadingScreenProps {
  roomId?: string;
}

const ReadingScreen: React.FC<ReadingScreenProps> = ({ roomId }) => {
  const [showProgressModal, setShowProgressModal] = useState(false);
  const [currentPage, setCurrentPage] = useState<number>(150);
  const [displayPage, setDisplayPage] = useState<number>(150);
  const [flipping, setFlipping] = useState<boolean>(false);
  const [flippingPage, setFlippingPage] = useState<number | null>(null);
  const [animating, setAnimating] = useState<boolean>(false);
  const [flipIntervalMinutes, setFlipIntervalMinutes] = useState<number>(3);
  const flipIntervalMs = flipIntervalMinutes * 60 * 1000;
  const [members, setMembers] = useState<{ name: string; page: number; color: string; userId: string }[]>([]);
  const [selfName, setSelfName] = useState<string>('');
  const [userId, setUserId] = useState<string | null>(null);

  // ユーザーID取得
  useEffect(() => {
    const uid = authStorage.getUserId();
    setUserId(uid);
    setSelfName(uid ? uid.substring(0, 1).toUpperCase() : 'A');
  }, []);

  // 他ユーザーの進捗を定期取得
  useEffect(() => {
    if (!roomId || !userId) return;
    const fetchState = async () => {
      try {
        const res = await readingStateApi.getRoomReadingState(roomId, userId);
        setMembers(
          res.userStates.map(u => ({
            name: u.userId.substring(0, 1).toUpperCase(),
            page: u.currentPage,
            color: u.userId === userId ? '#2196f3' : '#222',
            userId: u.userId
          }))
        );
        // 自分の進捗も同期
        const me = res.userStates.find(u => u.userId === userId);
        if (me) {
          setCurrentPage(me.currentPage);
          setDisplayPage(me.currentPage);
        }
      } catch (e) {}
    };
    fetchState();
    const interval = setInterval(fetchState, 5000);
    return () => clearInterval(interval);
  }, [roomId, userId]);

  // 自動めくり初回
  useEffect(() => {
    if (flipping && flippingPage === null && displayPage < maxPage) {
      const timer = setTimeout(() => {
        setFlippingPage(displayPage + 1);
      }, flipIntervalMs);
      return () => clearTimeout(timer);
    }
  }, [flipping, flippingPage, displayPage, flipIntervalMs]);

  // flippingPage がセットされたら、次のフレームで animate クラスを付与
  useEffect(() => {
    if (flippingPage !== null) {
      requestAnimationFrame(() => {
        setAnimating(true);
      });
    }
  }, [flippingPage]);

  // アニメーション終了時のシーケンス
  const onFlipEnd = async () => {
    setAnimating(false);
    const next = flippingPage!;
    setDisplayPage(next);
    setCurrentPage(next);
    setFlippingPage(null);
    // サーバーに進捗送信
    if (roomId && userId) {
      await readingStateApi.updateUserReadingState(roomId, userId, { userId, currentPage: next, comment: '' });
    }
    if (flipping && next < maxPage) {
      const timer = setTimeout(() => {
        setFlippingPage(next + 1);
      }, flipIntervalMs);
      // クリーンアップは省略
    } else {
      setFlipping(false);
    }
  };

  // 進捗率・メンバーアイコンの配置データ
  const progressPercent = currentPage / maxPage;
  const memberProgress = members.map((m) => ({
    ...m,
    percent: m.page / maxPage,
    isMe: m.userId === userId,
  }));

  return (
    <div className="container">
      <div className="mainWrapper">
        {/* 進捗バー＋メンバーアイコン */}
        <div className="progressWrapper">
          <div className="progressBar">
            <div
              className="progress"
              style={{ width: `${progressPercent * 100}%` }}
            ></div>
          </div>
          {memberProgress.map((m) => (
            <div
              key={m.userId}
              className="memberIcon"
              style={{ left: `calc(${320 * m.percent}px - 15px)`, background: m.color }}
            >
              {m.name}
            </div>
          ))}
        </div>

        {/* 本の表示エリア */}
        <div className="bookContainer">
          <div className="leftPage"></div>
          <div className="rightPage"></div>
          <div className="spine"></div>
          {flippingPage !== null && (
            <div
              className={`pageFlip ${animating ? "animate" : ""}`}
              onAnimationEnd={onFlipEnd}
            >
              <div className="back"></div>
            </div>
          )}
        </div>

        {/* ページ数表示 */}
        <div className="pageCount">
          {displayPage + 1} / {maxPage}
        </div>

        {/* 操作エリア */}
        <div className="controls">
          <label className="flipIntervalLabel">
            <input
              type="number"
              min="1"
              value={flipIntervalMinutes}
              onChange={(e) => setFlipIntervalMinutes(Number(e.target.value))}
              placeholder="分単位"
              className="intervalInput"
            />
            <span> 分に一回</span>
          </label>
          <button className="controlButton" onClick={() => setShowProgressModal(true)}>
            進捗入力
          </button>
          <button
            className="controlButton"
            onClick={() => {
              setFlipping((f) => {
                if (f) {
                  setFlippingPage(null);
                  setAnimating(false);
                }
                return !f;
              });
            }}
          >
            {flipping ? "自動めくり停止" : "自動めくり開始"}
          </button>
          {roomId && (
            <button
              className="controlButton"
              onClick={() => (window.location.href = `/rooms/${roomId}/chat`)}
            >
              💬 チャットに戻る
            </button>
          )}
        </div>
      </div>

      {/* 進捗入力モーダル */}
      {showProgressModal && (
        <ReadingProgressModal
          open={showProgressModal}
          currentPage={currentPage}
          maxPage={maxPage}
          onClose={() => setShowProgressModal(false)}
          onSubmit={async (page) => {
            setCurrentPage(page);
            setDisplayPage(page);
            setShowProgressModal(false);
            if (roomId && userId) {
              await readingStateApi.updateUserReadingState(roomId, userId, { userId, currentPage: page, comment: '' });
            }
          }}
        />
      )}

      <style jsx>{`
        .container {
          width: 100vw;
          min-height: 100vh;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          background-color: var(--green-bg);
          overflow: hidden;
          text-align: center;
        }
        .mainWrapper {
          width: 100%;
          max-width: 600px;
          margin: 0 auto;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
        }
        .progressWrapper {
          position: relative;
          width: 320px;
          height: 40px;
          margin-bottom: 16px;
          margin-left: auto;
          margin-right: auto;
        }
        .progressBar {
          position: absolute;
          top: 10px;
          left: 0;
          width: 100%;
          height: 12px;
          border-radius: 6px;
          background-color: var(--green-bg);
          overflow: hidden;
          border: 1px solid var(--border);
        }
        .progress {
          height: 100%;
          background-color: var(--green-main);
          border-radius: 6px;
        }
        .memberIcon {
          position: absolute;
          top: 0;
          width: 30px;
          height: 30px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: bold;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
          z-index: 2;
          font-size: 16px;
          transition: left 0.3s ease-out;
          background: var(--white);
          border: 1px solid var(--border);
          color: var(--green-dark);
        }
        .bookContainer {
          position: relative;
          width: 450px;
          height: 300px;
          perspective: 1000px;
          display: flex;
          justify-content: center;
          align-items: center;
          margin-left: auto;
          margin-right: auto;
        }
        .leftPage {
          position: absolute;
          left: 50%;
          transform: translateX(-100%);
          width: 200px;
          height: 280px;
          background-color: var(--white);
          border: 1px solid #ccc;
          border-right: none;
          box-shadow: inset 5px 0 15px rgba(0, 0, 0, 0.1);
          border-radius: 2px 0 0 2px;
          z-index: 3;
        }
        .rightPage {
          position: absolute;
          left: 50%;
          width: 200px;
          height: 280px;
          background-color: var(--white);
          border: 1px solid #ccc;
          border-left: none;
          box-shadow: inset -5px 0 15px rgba(0, 0, 0, 0.1);
          border-radius: 0 2px 2px 0;
          z-index: 2;
        }
        .spine {
          position: absolute;
          left: 50%;
          top: 50%;
          transform: translate(-50%, -50%);
          width: 10px;
          height: 280px;
          background-color: #8b4513;
          z-index: 5;
          box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
        }
        .pageFlip {
          position: absolute;
          left: 50%;
          top: 50%;
          transform-origin: 100% 50%;
          width: 200px;
          height: 280px;
          background: var(--white);
          border: 1px solid #ccc;
          backface-visibility: hidden;
          border-radius: 2px 0 0 2px;
          transform: translate(-100%, -50%) rotateY(0deg);
          z-index: 10;
          transition: box-shadow 0.3s ease-out;
          transform-style: preserve-3d;
        }
        .animate {
          animation: pageFlip 1.5s cubic-bezier(0.4, 0, 0.2, 1) forwards;
          box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
        }
        @keyframes pageFlip {
          0% {
            transform: translate(-100%, -50%) rotateY(0deg);
          }
          100% {
            transform: translate(-100%, -50%) rotateY(180deg);
          }
        }
        .back {
          position: absolute;
          left: 0;
          top: 0;
          width: 100%;
          height: 100%;
          background: var(--white);
          transform: rotateY(180deg);
          backface-visibility: hidden;
          border-radius: 2px 0 0 2px;
        }
        .pageCount {
          margin-top: 8px;
          font-size: 1.125rem;
          width: 100%;
          text-align: center;
        }
        .controls {
          display: flex;
          align-items: center;
          margin-top: 32px;
          gap: 8px;
          flex-wrap: wrap;
          justify-content: center;
          width: 100%;
          max-width: 600px;
          margin-left: auto;
          margin-right: auto;
        }
        .intervalInput {
          padding: 12px;
          border-radius: 8px;
          border: 1px solid var(--border);
          font-size: 18px;
          width: 120px;
          background: var(--green-bg);
          color: var(--accent);
        }
        .controlButton {
          padding: 12px 24px;
          border-radius: 8px;
          border: 1px solid var(--border);
          font-size: 18px;
          cursor: pointer;
          font-weight: bold;
          background: var(--green-bg);
          color: var(--accent);
        }
        .flipIntervalLabel {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      `}</style>
      <style jsx global>{`
        html, body, #__next {
          margin: 0;
          padding: 0;
          width: 100%;
          height: 100%;
          overflow-x: hidden;
          background-color: var(--green-bg);
        }
      `}</style>
    </div>
  );
};

export default ReadingScreen;
