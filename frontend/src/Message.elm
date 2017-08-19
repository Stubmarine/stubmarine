module Message exposing (..)

import RemoteData exposing (WebData)

import Model exposing (EmailList, EmailId, Email)

type Msg
  = HelloWorld
  | UpdateEmails (WebData EmailList)
  | SelectEmail EmailId
  | UpdateEmail (WebData Email)
  | WSEmailsMessage String
