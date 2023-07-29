# Notify

A notification broadcasting system that allows users to send categorized messages to multiple
channels.

## Architecture
    
There is a single executable application serving a RestAPI and a server side generated simple Web App.

The endpoints available are for the Submission of Messages and fetching the Log History.

Data from the messages are persisted in PostgreSQL DB.
Configuration is managed by Aero, credentials aefined according to a config.edn file via JDBC driver.
    For building the SQL queries
    The HTTP server 

## Dependencies / Stack

- Clojure
- Compojure
- Ring
- Cheschire
- Aero
- Component
- JDBC
- Hiccup

## API Endpoints
    send-message: Accepts POST requests with a body containing and an Web GUI page. 
    




## License

Copyright © 2023 Victor Inacio

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
