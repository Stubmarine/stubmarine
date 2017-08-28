module Message exposing (..)

import RemoteData exposing (WebData)

import Model exposing (EmailList, EmailId, Email, EndpointList, Route)

type Msg
  = FormInputInboxName String

  | ChangeRoute Route
  | SelectInbox String

  | UpdateEmails (WebData EmailList)

  | SelectEmail EmailId
  | UpdateEmail (WebData Email)

  | UpdateEndpoints (WebData EndpointList)

  | WSEmailsMessage String
