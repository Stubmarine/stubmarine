module Model exposing (..)

import RemoteData exposing (WebData)
import Char exposing (isLower, isUpper, isDigit)

type alias Model =
  { wsapiBasePath: String
  , route: Route
  , inboxName: InboxName
  , emails: WebData EmailList
  , email: WebData Email
  , endpoints: WebData EndpointList
  }

type InboxRoute
  = Emails
  | Endpoints

type Route
  = Landing
  | Inbox InboxName InboxRoute

type alias InboxName = String

isValidInboxName : String -> Bool
isValidInboxName name =
  let
    meetsLengthRequirement = (String.length name) > 1

    validChar = \c -> (isLower c) || (isUpper c) || (isDigit c) || c == '-'

    allCharsValid =
      name
       |> String.toList
       |> List.all validChar
  in
    meetsLengthRequirement && allCharsValid

type alias EmailId = Int

type alias Email =
  { id: EmailId
  , to: String
  , from: String
  , subject: String
  , body: String
  }

type alias EmailList = List Email

type alias Endpoint =
  { id: String
  , name: String
  , originalHost: String
  , newHost: String
  , newToken: String
  , example: String
  }

type alias EndpointList = List Endpoint
