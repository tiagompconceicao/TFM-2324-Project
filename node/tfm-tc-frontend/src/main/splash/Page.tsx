import { getAuth, getIdToken } from "firebase/auth"
import { useState } from "react"
import { useNavigate } from "react-router-dom"
import * as API from '../common/FetchUtils'
import { Service } from "../home/Service"
import './Page.css'

type APIUpdate = API.Request<String>
type APIInfo = API.FetchInfo<String>

type PageProps = {
    service: Service
  }

export const Splash = (props: PageProps) => {
  const navigate = useNavigate()

  const [apiInfo, setApiInfo] = useState<APIInfo| undefined>()
  const [apiUpdate, setApiUpdate] = useState<APIUpdate>()

  async function getUserIdToken(){
    const user = getAuth().currentUser
    if (user == null||undefined) throw ("User not found")
    return await getIdToken(user,true)
  }

  async function sendRequest() {
    try {
    await props.service.pingAPI(await getUserIdToken()).send()
    }
    catch(reason) {
      setApiInfo({ status: API.FetchState.ERROR })
    }
  }

  async function sendRequest2() {
    try {
      await props.service.getChat("Cwk8gFEoJxcCPDSPXHUY",await getUserIdToken()).send()
    }
    catch(reason) {
      setApiInfo({ status: API.FetchState.ERROR })
    }
  }

  async function sendRequest3() {
    try {
      await props.service.getChats(await getUserIdToken()).send()
    }
    catch(reason) {
      setApiInfo({ status: API.FetchState.ERROR })
    }
  }

  const onButtonClick = () => {
    navigate('/login');
  }
  const onButtonClick2 = () => {
    navigate('/home');
  }
  const onButtonClick3 = () => {
    sendRequest()
  }
  const onButtonClick4 = () => {
    sendRequest2()
  }
  const onButtonClick5 = () => {
    sendRequest3()
  }

  return (
    <div className="mainContainer">
      <div className={'titleContainer'}>
        <div>Welcome!</div>
      </div>
      <div>This is the home page.</div>
      <div className={'buttonContainer'}>
        <input
          className={'inputButton'}
          type="button"
          onClick={onButtonClick}
          value={'Log in'}/>
          <input
          className={'inputButton'}
          type="button"
          onClick={onButtonClick2}
          value={'Go home'}/>
          <input
          className={'inputButton'}
          type="button"
          onClick={onButtonClick3}
          value={'Test api'}/>
          <input
          className={'inputButton'}
          type="button"
          onClick={onButtonClick4}
          value={'Test api - get Chat'}/>
          <input
          className={'inputButton'}
          type="button"
          onClick={onButtonClick5}
          value={'Test api - get all Chats'}/>
      </div>
    </div>
  )
}