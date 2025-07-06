"use client"

import React, { useEffect, useRef, useState } from 'react'
import { MdCheck, MdKeyboardArrowDown } from 'react-icons/md'

interface Option {
    value: string
    label: string
}

interface CustomDropdownProps {
    options: Option[]
    value: string
    onChange: (value: string) => void
    placeholder?: string
    style?: React.CSSProperties
    disabled?: boolean
}

const CustomDropdown: React.FC<CustomDropdownProps> = ({
    options,
    value,
    onChange,
    placeholder = "選択してください",
    style = {},
    disabled = false
}) => {
    const [isOpen, setIsOpen] = useState(false)
    const [searchTerm, setSearchTerm] = useState('')
    const dropdownRef = useRef<HTMLDivElement>(null)
    const inputRef = useRef<HTMLInputElement>(null)

    // 選択された項目のラベルを取得
    const selectedOption = options.find(opt => opt.value === value)
    const selectedLabel = selectedOption ? selectedOption.label : placeholder

    // フィルタリングされたオプション
    const filteredOptions = options.filter(option =>
        option.label.toLowerCase().includes(searchTerm.toLowerCase())
    )

    // 外部クリックでドロップダウンを閉じる
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false)
                setSearchTerm('')
            }
        }

        document.addEventListener('mousedown', handleClickOutside)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const handleToggle = () => {
        if (disabled) return
        setIsOpen(!isOpen)
        if (!isOpen) {
            // ドロップダウンを開いたときに検索フィールドにフォーカス
            setTimeout(() => inputRef.current?.focus(), 10)
        }
    }

    const handleOptionClick = (optionValue: string) => {
        onChange(optionValue)
        setIsOpen(false)
        setSearchTerm('')
    }

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === 'Escape') {
            setIsOpen(false)
            setSearchTerm('')
        } else if (e.key === 'Enter') {
            e.preventDefault()
            if (filteredOptions.length === 1) {
                handleOptionClick(filteredOptions[0].value)
            }
        }
    }

    return (
        <div
            ref={dropdownRef}
            style={{
                position: 'relative',
                width: '100%',
                ...style
            }}
        >
            {/* ドロップダウンのトリガー */}
            <div
                onClick={handleToggle}
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '10px 12px',
                    border: '2px solid',
                    borderColor: isOpen ? '#2196f3' : '#e0e0e0',
                    borderRadius: '8px',
                    background: disabled ? '#f5f5f5' : '#ffffff',
                    cursor: disabled ? 'not-allowed' : 'pointer',
                    transition: 'all 0.2s ease',
                    fontSize: '14px',
                    color: disabled ? '#999' : selectedOption ? '#333' : '#666',
                    boxShadow: isOpen ? '0 0 0 3px rgba(33, 150, 243, 0.1)' : 'none'
                }}
            >
                <span style={{
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                    fontWeight: selectedOption ? '500' : '400'
                }}>
                    {selectedLabel}
                </span>
                <MdKeyboardArrowDown
                    style={{
                        marginLeft: 8,
                        fontSize: 20,
                        transform: isOpen ? 'rotate(180deg)' : 'rotate(0deg)',
                        transition: 'transform 0.2s ease',
                        color: disabled ? '#999' : '#666'
                    }}
                />
            </div>

            {/* ドロップダウンメニュー */}
            {isOpen && !disabled && (
                <div
                    style={{
                        position: 'absolute',
                        top: '100%',
                        left: 0,
                        right: 0,
                        zIndex: 1000,
                        background: '#ffffff',
                        border: '2px solid #2196f3',
                        borderTop: 'none',
                        borderRadius: '0 0 8px 8px',
                        maxHeight: '300px',
                        overflowY: 'auto',
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)'
                    }}
                >
                    {/* 検索フィールド */}
                    {options.length > 5 && (
                        <div style={{ padding: '8px', borderBottom: '1px solid #e0e0e0' }}>
                            <input
                                ref={inputRef}
                                type="text"
                                placeholder="検索..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                onKeyDown={handleKeyDown}
                                style={{
                                    width: '100%',
                                    padding: '6px 8px',
                                    border: '1px solid #e0e0e0',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    outline: 'none'
                                }}
                            />
                        </div>
                    )}

                    {/* オプション一覧 */}
                    <div>
                        {filteredOptions.length === 0 ? (
                            <div style={{
                                padding: '12px',
                                color: '#999',
                                textAlign: 'center',
                                fontSize: '14px'
                            }}>
                                該当する項目がありません
                            </div>
                        ) : (
                            filteredOptions.map((option) => (
                                <div
                                    key={option.value}
                                    onClick={() => handleOptionClick(option.value)}
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'space-between',
                                        padding: '10px 12px',
                                        cursor: 'pointer',
                                        background: option.value === value ? '#e3f2fd' : 'transparent',
                                        color: option.value === value ? '#1976d2' : '#333',
                                        fontSize: '14px',
                                        fontWeight: option.value === value ? '500' : '400',
                                        transition: 'background 0.15s ease',
                                        borderBottom: '1px solid #f0f0f0'
                                    }}
                                    onMouseEnter={(e) => {
                                        if (option.value !== value) {
                                            e.currentTarget.style.background = '#f5f5f5'
                                        }
                                    }}
                                    onMouseLeave={(e) => {
                                        if (option.value !== value) {
                                            e.currentTarget.style.background = 'transparent'
                                        }
                                    }}
                                >
                                    <span>{option.label}</span>
                                    {option.value === value && (
                                        <MdCheck style={{ fontSize: 18, color: '#1976d2' }} />
                                    )}
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
        </div>
    )
}

export default CustomDropdown
