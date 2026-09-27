# anderix-otcs

Command-line utilities for OpenText Content Server, the enterprise document and
records management system that was sold as Livelink for most of its life. Content
Server administration is a web UI, and the UI is built around one node at a time:
it will show you the permissions on a folder, but not every permission under a
node in one pass, and its bulk import reads an XML control file that nothing in
the product will generate for you from a directory tree. These utilities do that
kind of work from a shell, against a live server, over Content Server's REST API.

They share `anderix-lib`, a Java library in this repository that handles
authentication, node traversal, permission reading and the HTTP and JSON
plumbing under them. It is built here rather than fetched, so the build order
below matters.

## Building

In order, because each step depends on the one before it:

    ./fetch-libs.sh     # third-party jars and DLLs into lib/
    ./build-lib.sh      # compiles anderix-lib into the jar the utilities expect
    cd otcs-util/<utility> && ./build.sh

`fetch-libs.sh` pulls Apache Commons Lang from Maven Central, and the .NET
assemblies `ImportWriter` needs — ZetaLongPaths, for reading paths longer than
Windows' traditional limit, and JetBrains.Annotations, which it depends on — from
`anderix.com/lib/`. Those are mirrored copies of third-party libraries, served
over HTTPS with no published checksum; substitute your own copies in `lib/` if you would rather not
trust the mirror.

The Java utilities need a JDK and nothing else. `ImportWriter` is C# and builds
with Mono's `mcs`, which is the reason for the .NET assemblies. Every script here
has a `.bat` twin for Windows; the shell form is used throughout this file.

Build artifacts and `*.properties` files are ignored by git, so a working tree
with real credentials in it stays clean.

## A first command

`GetFoldersAndPermissions` reads and writes nothing on the server, which makes it
the safe one to start with. Each utility's `run.sh` carries a worked invocation
with placeholder arguments, so the classpath is written down once and not here:

    ./fetch-libs.sh && ./build-lib.sh
    cd otcs-util/GetFoldersAndPermissions
    ./build.sh
    ./run.sh            # after replacing the server name and node id in it

It prompts for a username and password, walks the tree under the node you name,
and reports each folder with the permissions on it. Given an output filename it
writes HTML; without one it prints to the console.

## The utilities

`GetFoldersAndPermissions` answers who has access to what under a node, as
described above. It is the reason the repository exists: an access review of a
large Content Server tree is otherwise a clicking exercise.

`RemovePublicAccess` strips the Public Access permission from a node and
everything beneath it, which is the remediation that usually follows such a
review.

`OtcsRest` is a scratchpad for the REST API. Give it a method and a path with any
querystring and it prints the response, which is how you check what the server
actually returns before writing anything against it. Quote the path if it
contains spaces or an ampersand.

`OtcsFolderSync` reconciles a Content Server folder against a local directory.
Its mode is the first argument: `sync` moves newer files in both directions,
`upload` sends local files up, `publish` brings server files down, and `deltas`
reports the differences without touching either side. `--test` on any mode shows
what would happen and changes nothing.

`ImportWriter` is the bulk-import side, and the only C# component. It walks a
local directory tree and writes the XML control file Content Server's import
reads, rewriting each local directory path into the Content Server path you give
it and carrying the created and modified times across. It splits the output into
batches so a large tree imports in pieces rather than one transaction.

## Credentials

`GetFoldersAndPermissions` and `RemovePublicAccess` prompt for a username and
password on the terminal, and take the server name as an argument.
`OtcsFolderSync` and `OtcsRest` instead read a `.properties` file named after the
class and sitting beside it, holding `server`, `username` and `password`; copy the
`.properties.example` next to each and fill it in.

Whichever way the credentials arrive, they are posted to the server's auth
endpoint over plain `http://`, and the scheme is written into the source rather
than configurable. Run these on a network you trust, or change the scheme where
the client is constructed. A Content Server account sees no more than it sees in
the web UI, so `RemovePublicAccess` can only remove permissions the signed-in
account could remove by hand.

## Licence

Licence terms are in `anderix-lib/license.txt`.
