module Model exposing (..)

import RemoteData exposing (WebData)

type alias Model =
  { emails: WebData EmailList
  , email: WebData Email
  }

type alias EmailId = Int

type alias EmailListItem =
    { id: EmailId
    , to: String
    , from: String
    , subject: String
    }

type alias Email =
    { id: EmailId
    , to: String
    , from: String
    , subject: String
    , body: String
    }

type alias EmailList = List EmailListItem
