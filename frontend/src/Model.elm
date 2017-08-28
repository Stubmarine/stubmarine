module Model exposing (..)

import RemoteData exposing (WebData)

type alias Model =
  { wsapiBasePath: String
  , route: Route
  , emails: WebData EmailList
  , email: WebData Email
  , endpoints: WebData EndpointList
  }

type Route = Emails | Endpoints

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
