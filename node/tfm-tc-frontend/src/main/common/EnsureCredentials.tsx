import { ReactNode, useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from './UseAuth'

/**
 * Type that specifies the props object for the EnsureCredentials component.
 */
type EnsureCredentialsProps = {
  loginPageRoute: string,
  children: ReactNode,
  requestParams?:string
}

/**
 * Component r
 * 
 * esponsible for verifying if the user has already entered his credentials.
 */


export const EnsureCredentials = (props : EnsureCredentialsProps) => {

    const { authState } = useAuth()
      
      if (authState.pending) {
        return <h1>waiting...</h1>
      }
    
      if (!authState.isSignedIn) {
        return (
            <Navigate to={props.loginPageRoute} /> 
        )
      }
    
      return (
        <> {props.children} </> 
      )

}

