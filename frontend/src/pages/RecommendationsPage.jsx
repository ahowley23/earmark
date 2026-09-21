import { useState, useEffect } from 'react'
import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_URL || ''

function BookCard({ rec }) {
  const [feedback, setFeedback] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleFeedback = async (liked) => {
    if (feedback !== null) return
    setLoading(true)
    try {
      await axios.post(`${API_BASE}/api/recommendations/${rec.id}/feedback?liked=${liked}`)
      setFeedback(liked)
    } catch (e) {
      console.error('Feedback failed', e)
    } finally {
      setLoading(false)
    }
  }

  const colors = [
    'bg-[#E8EEF5]', 'bg-[#E6F0EC]', 'bg-[#EDE6F0]',
    'bg-[#F0EDE6]', 'bg-[#F0E6E6]'
  ]
  const colorIndex = rec.bookTitle.length % colors.length

  return (
    <div className="bg-white border border-[#E2E2DC] rounded-xl overflow-hidden flex flex-col hover:shadow-md transition-shadow">
      <div className={`${colors[colorIndex]} h-48 flex items-center justify-center`}>
        <div className="text-center px-6">
          <p className="font-serif text-[#2D4A6B] text-lg leading-tight">
            {rec.bookTitle}
          </p>
          <p className="text-[#6B7280] text-xs mt-1">{rec.bookAuthor}</p>
        </div>
      </div>

      <div className="p-5 flex flex-col flex-1">
        <h3 className="font-serif text-[#111111] text-lg leading-tight mb-1">
          {rec.bookTitle}
        </h3>
        <p className="text-[#6B7280] text-sm mb-3">{rec.bookAuthor}</p>

        <span className="inline-block font-mono text-[11px] px-2 py-0.5 rounded bg-[#E6F0EC] text-[#3D6B5A] w-fit mb-3">
          {rec.engine}
        </span>

        <p className="text-[#333] text-sm leading-relaxed flex-1">
          {rec.reasoning}
        </p>

        <div className="flex gap-2 mt-4 pt-4 border-t border-[#E2E2DC]">
          <button
            onClick={() => handleFeedback(true)}
            disabled={loading || feedback !== null}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-all ${
              feedback === true
                ? 'bg-[#3D6B5A] text-white'
                : feedback === false
                ? 'bg-[#F8F8F6] text-[#6B7280]'
                : 'bg-[#F8F8F6] text-[#111111] hover:bg-[#E6F0EC] hover:text-[#3D6B5A]'
            }`}
          >
            👍 Like
          </button>
          <button
            onClick={() => handleFeedback(false)}
            disabled={loading || feedback !== null}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-all ${
              feedback === false
                ? 'bg-[#6B3D3D] text-white'
                : feedback === true
                ? 'bg-[#F8F8F6] text-[#6B7280]'
                : 'bg-[#F8F8F6] text-[#111111] hover:bg-[#F0E6E6] hover:text-[#6B3D3D]'
            }`}
          >
            👎 Pass
          </button>
        </div>
      </div>
    </div>
  )
}

export default function RecommendationsPage({ user, onLogout }) {
  const [recommendations, setRecommendations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetchRecommendations()
  }, [])

  const fetchRecommendations = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await axios.get(`${API_BASE}/api/spotify/recommendations`)
      setRecommendations(res.data)
    } catch (e) {
      if (e.response?.status === 401) {
        onLogout()
      } else {
        setError('Failed to load recommendations. Please try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-[#F8F8F6]">
      <nav className="border-b border-[#E2E2DC] bg-white px-6 py-4 flex justify-between items-center">
        <h1 className="font-serif text-xl text-[#111111]">Earmark</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-[#6B7280]">{user}</span>
          <button
            onClick={onLogout}
            className="text-sm text-[#6B7280] hover:text-[#111111] transition-colors"
          >
            Sign out
          </button>
        </div>
      </nav>

      <main className="max-w-5xl mx-auto px-6 py-10">
        <div className="mb-10">
          <p className="font-mono text-xs text-[#6B7280] tracking-widest uppercase mb-3">
            Based on your Spotify taste
          </p>
          <h2 className="font-serif text-4xl text-[#111111] mb-3">
            Your reading list.
          </h2>
          <p className="text-[#6B7280] text-sm">
            5 books matched to your music — rate them to improve future recommendations.
          </p>
        </div>

        {loading && (
          <div className="text-center py-20">
            <p className="text-[#6B7280] text-sm animate-pulse">
              Analyzing your taste and finding books...
            </p>
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-600 text-sm">{error}</p>
            <button
              onClick={fetchRecommendations}
              className="text-red-600 text-sm underline mt-2"
            >
              Try again
            </button>
          </div>
        )}

        {!loading && !error && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {recommendations.map(rec => (
              <BookCard key={rec.id} rec={rec} />
            ))}
          </div>
        )}

        {!loading && recommendations.length > 0 && (
          <div className="text-center mt-10">
            <button
              onClick={fetchRecommendations}
              className="text-sm text-[#6B7280] hover:text-[#111111] underline transition-colors"
            >
              Refresh recommendations
            </button>
          </div>
        )}
      </main>
    </div>
  )
}