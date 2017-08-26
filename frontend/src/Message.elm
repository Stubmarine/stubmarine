module Message exposing (..)

import RemoteData exposing (WebData)

import Model exposing (EmailList, EmailId, Email, EndpointList, Route)

type Msg
  = HelloWorld
  | ChangeRoute Route

  | FetchEmails
  | UpdateEmails (WebData EmailList)

  | SelectEmail EmailId
  | UpdateEmail (WebData Email)

  | FetchEndpoints
  | UpdateEndpoints (WebData EndpointList)

  | WSEmailsMessage String
