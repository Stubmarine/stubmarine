module Subscriptions exposing (subscriptions)

import Model exposing (Model)
import Message exposing (Msg, Msg(WSEmailsMessage))
import WebSocket

subscriptions : Model -> Sub Msg
subscriptions model =
  WebSocket.listen "ws://localhost:3000/wsapi/emails" WSEmailsMessage
