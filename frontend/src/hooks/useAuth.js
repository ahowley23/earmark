import { useState, useEffect } from 'react'
import axios from 'axios'

export function useAuth() {
  const [token, setToken] = useState(null)
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Set axios default header whenever token changes
  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    } else {
      delete axios.defaults.headers.common['Authorization']
    }
  }, [token])

  const register = async (email, password) => {
    setLoading(true)
    setError(null)
    try {
      const res = await axios.post('/api/auth/register', { email, password })
      setToken(res.data.token)
      setUser(res.data.email)
      return true
    } catch (e) {
      setError(e.response?.data?.message || 'Registration failed')
      return false
    } finally {
      setLoading(false)
    }
  }

  const login = async (email, password) => {
    setLoading(true)
    setError(null)
    try {
      const res = await axios.post('/api/auth/login', { email, password })
      setToken(res.data.token)
      setUser(res.data.email)
      return true
    } catch (e) {
      setError(e.response?.data?.message || 'Login failed')
      return false
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    setToken(null)
    setUser(null)
  }

  return { token, user, loading, error, register, login, logout }
}