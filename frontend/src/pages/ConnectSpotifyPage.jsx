import { useState } from 'react'
import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_URL || ''

export default function ConnectSpotifyPage({ user, onConnected }) {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleConnect = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await axios.get(`${API_BASE}/api/spotify/connect`)
      window.location.href = res.data
    } catch (e) {
      setError('Failed to connect to Spotify. Please try again.')
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-[#F8F8F6] flex items-center justify-center px-4">
      <div className="w-full max-w-md text-center">
        <h1 className="text-4xl font-serif text-[#111111] mb-4">
          Connect Spotify
        </h1>
        <p className="text-[#6B7280] mb-2">
          Signed in as <span className="text-[#111111] font-medium">{user}</span>
        </p>
        <p className="text-[#6B7280] text-sm mb-10 max-w-sm mx-auto leading-relaxed">
          Earmark reads your top artists to build a taste profile, then recommends books that match your music.
        </p>

        <div className="bg-white border border-[#E2E2DC] rounded-xl p-6 mb-8 text-left">
          <p className="text-xs font-mono text-[#6B7280] uppercase tracking-widest mb-4">
            What we access
          </p>
          <div className="space-y-3">
            {[
              'Your top artists across short, medium, and long term',
              'Nothing else — no playlist edits, no playback control',
            ].map((item, i) => (
              <div key={i} className="flex items-start gap-3">
                <span className="text-[#3D6B5A] mt-0.5">✓</span>
                <span className="text-sm text-[#333]">{item}</span>
              </div>
            ))}
          </div>
        </div>

        {error && (
          <p className="text-red-500 text-sm mb-4">{error}</p>
        )}

        <button
          onClick={handleConnect}
          disabled={loading}
          className="w-full py-3 bg-[#1DB954] text-white text-sm font-medium rounded-lg hover:bg-[#1aa34a] transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
        >
          {loading ? 'Redirecting...' : '🎵 Connect with Spotify'}
        </button>

        <p className="text-xs text-[#6B7280] mt-4">
          You'll be redirected to Spotify to approve access.
        </p>
      </div>
    </div>
  )
}