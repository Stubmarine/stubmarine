module Routing exposing (..)

import Navigation exposing (Location)
import UrlParser exposing (..)

import Model exposing (Route(..), InboxName)

matchers : Parser (Route -> a) a
matchers =
  oneOf
    [ map LandingRoute top
    , map InboxRoute (s "inbox" </> string)
    ]

parseLocation : Location -> Route
parseLocation location =
  case (parsePath matchers location) of
    Just route ->
      route
    Nothing ->
      NotFoundRoute

landingRoutePath : String
landingRoutePath = "/"

inboxPath : InboxName -> String
inboxPath inboxName = "/inbox/" ++ inboxName
