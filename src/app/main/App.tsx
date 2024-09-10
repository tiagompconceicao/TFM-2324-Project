import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Login from './login/Page'
import Home from './home/Page'
//import './App.css'
import { useEffect, useState } from 'react'

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [email, setEmail] = useState('')

  //setLoggedIn={setLoggedIn} setEmail={setEmail}

  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home email={email} loggedIn={loggedIn} />} />
          <Route path="/login" element={<Login  />} />
        </Routes>
      </BrowserRouter>
    </div>
  )
}

export default App