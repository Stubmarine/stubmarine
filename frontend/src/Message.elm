module Message exposing (..)

import RemoteData exposing (WebData)

import Model exposing (EmailList, EmailId, Email, Route)

type Msg
  = HelloWorld
  | ChangeRoute Route
  | UpdateEmails (WebData EmailList)
  | SelectEmail EmailId
  | UpdateEmail (WebData Email)
  | WSEmailsMessage String
