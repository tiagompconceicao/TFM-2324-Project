import { useState, useEffect } from 'react'
import { User, getAuth, onAuthStateChanged } from 'firebase/auth'

type UserSession= {
    isSignedIn: boolean,
    pending: boolean,
    user: User | null,
}

export function useAuth() {
  const [authState, setAuthState] = useState<UserSession>({
    isSignedIn: false,
    pending: true,
    user: null,
  })

  useEffect(() => {
    const unregisterAuthObserver = onAuthStateChanged(getAuth() , (user) =>
      setAuthState({ user, pending: false, isSignedIn: !!user })
    )
    return () => unregisterAuthObserver()
  }, [])

  return { authState }
}