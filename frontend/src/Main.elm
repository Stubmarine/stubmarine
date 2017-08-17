port module Main exposing (..)

import Json.Decode as Decode
import Json.Decode.Extra exposing ((|:))
import Html exposing (Html, div, text)
import Html.Attributes exposing (class)
import Http
import RemoteData exposing (sendRequest, RemoteData(..), WebData)
import WebSocket



type Msg
  = HelloWorld
  | UpdateEmails (WebData Emails)
  | WSEmailsMessage String


type alias Model =
  { emails: WebData Emails
  }


type alias Email =
  { id: Int
  , to: String
  , from: String
  , subject: String
  }

type alias Emails = List Email

type alias BrowserNotification =
  { title: String
  , body: String
  }

port notifyBrowser : BrowserNotification -> Cmd msg


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
  case msg of
    HelloWorld ->
      ( model, Cmd.none )
    UpdateEmails response ->
      ( { model | emails = response }, Cmd.none )
    WSEmailsMessage newEmailStr ->
      let
        newEmailResult = Decode.decodeString decodeEmail newEmailStr
        existingEmails = case model.emails of
          Success existing ->
            existing
          _ ->
            []
      in
        case newEmailResult of
          Ok newEmail ->
            ( { model | emails = (Success (existingEmails ++ [newEmail])) }
            , notifyBrowser (BrowserNotification newEmail.from newEmail.subject) )
          _ ->
            ( model, Cmd.none )

decodeEmails : Decode.Decoder Emails
decodeEmails =
  Decode.list decodeEmail

decodeEmail : Decode.Decoder Email
decodeEmail =
  Decode.succeed Email
    |: (Decode.field "id" Decode.int)
    |: (Decode.field "to" Decode.string)
    |: (Decode.field "from" Decode.string)
    |: (Decode.field "subject" Decode.string)

fetchEmails : Cmd Msg
fetchEmails =
  Http.request
    { method = "GET"
    , headers = [ ]
    , url = "/api/emails"
    , body = Http.emptyBody
    , expect = Http.expectJson decodeEmails
    , timeout = Nothing
    , withCredentials = False
    }
    |> RemoteData.sendRequest
    |> Cmd.map UpdateEmails

viewEmail : Email -> Html Msg
viewEmail email =
  div [ class "email" ]
    [ div [] [ text "To: ", text email.to ]
    , div [] [ text "From: ", text email.from ]
    , div [] [ text "Subject: ", text email.subject ]
    ]

view : Model -> Html Msg
view model =
  let
    emails = case model.emails of
      Success emailsList ->
        List.map viewEmail emailsList
      Loading ->
        [ div [] [ text "Loading..." ] ]
      _ ->
        [ div [] [] ]
  in
    div [] emails

subscriptions : Model -> Sub Msg
subscriptions model =
  WebSocket.listen "ws://localhost:8080/wsapi/emails" WSEmailsMessage

init : ( Model, Cmd Msg )
init = ( Model Loading, fetchEmails )

main : Program Never Model Msg
main =
  Html.program { init = init, view = view, update = update, subscriptions = subscriptions }