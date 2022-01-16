CREATE KEYSPACE IF NOT EXISTS cinema
    WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };
