# Minestom DHSupport

This repository is a port of the [DHSupport plugin](https://gitlab.com/distant-horizons-team/distant-horizons-server-plugin) for [Minestom](https://minestom.net).
It is a library for the Distant Horizons mod, providing a server API for sending LOD data to clients.

## Features
- **Level Identification**: Each level has a unique identifier that can be used to identify it, this allows for clients to cache LOD data.
- **Remote Client Configuration**: Clients send their configuration to the server, 
the server can then complement this configuration with additional settings like max view distance and world border settings.
- **LOD Data**: The client can request LOD data from the server and the server will send the data to the client.

## Not Supported
- **LOD Generation**: The library does not generate any LOD data on its own. The user of the library is responsible for providing the pre-generated DHSupport plugin database.
- **Real-time Updates**: The same as above, the library does not generate any LOD data and therefore does not support real-time updates.

## License
This project is a derivative work of the Distant Horizons server plugin, which is licensed under the [GNU General Public License](https://gitlab.com/distant-horizons-team/distant-horizons-server-plugin/-/blob/f55d6441440cda75629e3f9204131d11ea488210/LICENSE).

### Attribution
This project is based on code from the [Distant Horizons Server Plugin](https://gitlab.com/distant-horizons-team/distant-horizons-server-plugin) by the Distant Horizons Team, which is licensed under the GPL.

Modifications and adaptations have been made to work with Minestom. As required by the GPL, the full source code of this project is made available under the same license.
