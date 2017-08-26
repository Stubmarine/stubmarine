import Html exposing (programWithFlags)
import RemoteData exposing (RemoteData(..))
import Task

import Message exposing (Msg, Msg(FetchEmails, FetchEndpoints))
import Model exposing (Model, Route(Emails))
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
  , route = Emails
  , emails = NotAsked
  , email = NotAsked
  , endpoints = NotAsked
  }

initMsg : Cmd Msg
initMsg =
  Cmd.batch
    [ send FetchEmails
    , send FetchEndpoints
    ]

init : Flags -> ( Model, Cmd Msg )
init flags =
  ( initModel flags, initMsg )

main : Program Flags Model Msg
main =
  programWithFlags
    { init = init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }