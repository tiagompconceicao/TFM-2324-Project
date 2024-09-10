"use client"

import { StrictMode } from "react";
import {createRoot} from 'react-dom/client'
//import './index.css'
import App from './main/App'

const root = createRoot(
  document.getElementById('root') as HTMLElement
)
root.render(
  <StrictMode>
    <App />
  </StrictMode>
)
