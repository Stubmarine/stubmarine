import RemoteData exposing (RemoteData(..))
import Task
import Navigation exposing (Location)

import Message exposing (Msg, Msg(OnLocationChange))
import Model exposing (Model, Route(LandingRoute))
import Subscriptions exposing (subscriptions)
import Update exposing (update, fetchEmailList)
import View exposing (view)


type alias Flags =
  { wsapiBasePath: String
  }

send : msg -> Cmd msg
send msg =
  Task.succeed msg
  |> Task.perform identity

initModel : Flags -> Model
initModel flags =
  { wsapiBasePath = flags.wsapiBasePath
  , route = LandingRoute
  , inboxName = ""
  , emails = NotAsked
  , email = NotAsked
  , endpoints = NotAsked
  }

initMsg : Location -> Cmd Msg
initMsg location =
  send (OnLocationChange location)

init : Flags -> Location -> ( Model, Cmd Msg )
init flags location =
  ( initModel flags, initMsg location )


main : Program Flags Model Msg
main =
    Navigation.programWithFlags OnLocationChange
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }