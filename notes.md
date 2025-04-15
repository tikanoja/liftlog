dev using postgresql
brew services start postgresql

check that it is running with
brew services list

go into the db
psql -U postgres -d liftlog

see the tables with
\dt

