module Message exposing (..)

import RemoteData exposing (WebData)
import Navigation exposing (Location)

import Model exposing (EmailList, EmailId, Email, EndpointList, Route, InboxPageSubRoute)

type Msg
  = FormInputInboxName String
  | GenerateRandomInboxName

  | ChangeInboxPageSubRoute InboxPageSubRoute

  | UpdateEmails (WebData EmailList)

  | SelectEmail EmailId
  | UpdateEmail (WebData Email)

  | UpdateEndpoints (WebData EndpointList)

  | WSEmailsMessage String

  | ChangeLocation String
  | OnLocationChange Location
