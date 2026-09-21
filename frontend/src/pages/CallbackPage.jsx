import { useEffect } from 'react'
import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_URL || ''

export default function CallbackPage({ onConnected }) {
  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const code = params.get('code')
    const state = params.get('state')

    if (!code || !state) {
      onConnected(false)
      return
    }

    axios.get(`${API_BASE}/api/spotify/callback?code=${code}&state=${state}`)
      .then(() => onConnected(true))
      .catch(() => onConnected(false))
  }, [])

  return (
    <div className="min-h-screen bg-[#F8F8F6] flex items-center justify-center">
      <div className="text-center">
        <p className="font-serif text-2xl text-[#111111] mb-2">
          Connecting Spotify...
        </p>
        <p className="text-[#6B7280] text-sm animate-pulse">
          Just a moment
        </p>
      </div>
    </div>
  )
}