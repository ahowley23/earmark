import { useState, useEffect } from 'react'
import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_URL || ''

export function useAuth() {
  const [token, setToken] = useState(null)
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

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
      const res = await axios.post(`${API_BASE}/api/auth/register`, { email, password })
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
      const res = await axios.post(`${API_BASE}/api/auth/login`, { email, password })
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