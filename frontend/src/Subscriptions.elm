module Subscriptions exposing (subscriptions)

import Model exposing (Model, Route(InboxRoute))
import Message exposing (Msg, Msg(WSEmailsMessage))
import WebSocket

subscriptions : Model -> Sub Msg
subscriptions model =
  let
    url = \inboxName -> model.wsapiBasePath ++ "/wsapi/inbox/" ++ inboxName ++ "/emails"

    email = case model.route of
      InboxRoute inboxName ->
         WebSocket.listen (url inboxName) WSEmailsMessage
      _ ->
        Sub.none
  in
    email
