import Html exposing (programWithFlags)
import RemoteData exposing (RemoteData(..))

import Message exposing (Msg)
import Model exposing (Model)
import Subscriptions exposing (subscriptions)
import Update exposing (update, fetchEmailList)
import View exposing (view)

type alias Flags =
  { apiHost: String
  }

init : Flags -> ( Model, Cmd Msg )
init flags = ( Model flags.apiHost Loading NotAsked, fetchEmailList )

main : Program Flags Model Msg
main =
  programWithFlags
    { init = init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }