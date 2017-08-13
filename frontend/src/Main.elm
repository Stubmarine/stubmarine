import Json.Decode as Decode
import Json.Decode.Extra exposing ((|:))
import Html exposing (Html, div, text)
import Html.Attributes exposing (class)
import Http
import RemoteData exposing (sendRequest, RemoteData(..), WebData)

type Msg
  = HelloWorld
  | UpdateEmails (WebData Emails)


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


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
  case msg of
    HelloWorld ->
      ( model, Cmd.none )
    UpdateEmails response ->
      ( { model | emails = response }, Cmd.none )

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
    Sub.none

init : ( Model, Cmd Msg )
init = ( Model Loading, fetchEmails )

main : Program Never Model Msg
main =
  Html.program { init = init, view = view, update = update, subscriptions = subscriptions }