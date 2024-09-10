import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Login from './login/Page'
import Home from './home/Page'
import {Splash } from './splash/Page'
import { EnsureCredentials } from './common/EnsureCredentials';
import firebase from 'firebase/compat/app';
import {firebaseConfig}  from './firebaseConfig';
import * as HomePage from './home/Page'
import Home2, * as HomePage2 from './bootstrapedHome/Page'


const API_BASE_URL = 'http://localhost:8080/api'
const loginPageRoute = '/login'


firebase.initializeApp(firebaseConfig);

function renderPromptsService(){
  return HomePage.createService(
    new URL(`${API_BASE_URL}/chats`),
    new URL(`${API_BASE_URL}/services`),
    new URL(`${API_BASE_URL}/ping`),
  )
}

function renderPromptsService2(){
  return HomePage2.createService(
    new URL(`${API_BASE_URL}/chats`),
    new URL(`${API_BASE_URL}/ping`)
  )
}

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={
            <EnsureCredentials loginPageRoute={loginPageRoute}>
              <Home service={renderPromptsService()}/>
            </EnsureCredentials>
            }/>
            <Route path="/test" element={
            <EnsureCredentials loginPageRoute={loginPageRoute}>
              <Home2 service={renderPromptsService2()}/>
            </EnsureCredentials>
            }/>
          <Route path={loginPageRoute} element={<Login/>} />
        </Routes>
      </BrowserRouter>
    </div>
  )
}

export default App


/*
<Route path="/tests" element={
    <EnsureCredentials loginPageRoute={loginPageRoute}>
      <Splash service={renderPromptsService()}/>
    </EnsureCredentials>
    }/>
*/