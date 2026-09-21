import { useState } from 'react'
import { useAuth } from './hooks/useAuth'
import AuthPage from './pages/AuthPage'
import ConnectSpotifyPage from './pages/ConnectSpotifyPage'
import RecommendationsPage from './pages/RecommendationsPage'
import CallbackPage from './pages/CallbackPage'

// Simple router based on current path and app state
// No react-router needed for this straightforward flow
function getPage(path, token, spotifyConnected) {
  if (path.startsWith('/callback')) return 'callback'
  if (!token) return 'auth'
  if (!spotifyConnected) return 'connect'
  return 'recommendations'
}

export default function App() {
  const { token, user, loading, error, register, login, logout } = useAuth()
  const [spotifyConnected, setSpotifyConnected] = useState(false)

  const path = window.location.pathname

  const handleAuth = async (mode, email, password) => {
    if (mode === 'login') return await login(email, password)
    return await register(email, password)
  }

  const handleSpotifyConnected = (success) => {
    if (success) {
      setSpotifyConnected(true)
      // Clean up the URL after callback
      window.history.replaceState({}, '', '/')
    } else {
      // Connection failed — go back to connect page
      window.history.replaceState({}, '', '/')
    }
  }

  const handleLogout = () => {
    logout()
    setSpotifyConnected(false)
  }

  const page = getPage(path, token, spotifyConnected)

  if (page === 'callback') {
    return <CallbackPage onConnected={handleSpotifyConnected} />
  }

  if (page === 'auth') {
    return <AuthPage onAuth={handleAuth} />
  }

  if (page === 'connect') {
    return <ConnectSpotifyPage user={user} onConnected={() => setSpotifyConnected(true)} />
  }

  return <RecommendationsPage user={user} onLogout={handleLogout} />
}
