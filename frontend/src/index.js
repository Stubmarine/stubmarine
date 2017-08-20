'use strict';

require('./css/main.scss');

// Require index.html so it gets copied to dist
require('./index.html');

const Elm = require('./Main.elm');
const mountNode = document.getElementById('main');

const isPws = window.location.host.match(/\.cfapps\.io/);
const wsProtocol = isPws ? 'wss' : 'ws';
const wsPort = isPws ? ':4443': '';

// .embed() can take an optional second argument. This would be an object describing the data we need to start a program, i.e. a userID or some token
Elm.Main.embed(mountNode, {
    wsapiBasePath: wsProtocol + '://' + window.location.host + wsPort
});
