import Html exposing (Html)
import RemoteData exposing (RemoteData(..))

import Message exposing (Msg)
import Model exposing (Model)
import Subscriptions exposing (subscriptions)
import Update exposing (update, fetchEmailList)
import View exposing (view)


init : ( Model, Cmd Msg )
init = ( Model Loading NotAsked, fetchEmailList )

main : Program Never Model Msg
main =
  Html.program
    { init = init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }