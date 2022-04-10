CREATE TABLE NODE_CONFIRMATIONS(id VARCHAR(64) PRIMARY KEY, request_id VARCHAR(32), concentrator VARCHAR(64) NOT NULL, node VARCHAR(16) NOT NULL, lan_id VARCHAR(4) NOT NULL, communication_net VARCHAR(8) NOT NULL, address VARCHAR(32) NOT NULL, confirmer VARCHAR(16), requested_time TIMESTAMP NOT NULL, expired_time TIMESTAMP NOT NULL, confirmed_time TIMESTAMP, canceled BOOLEAN DEFAULT FALSE NOT NULL);
CREATE TABLE CONCENTRATIONS(id VARCHAR(64) PRIMARY KEY, concentrator VARCHAR(64) NOT NULL, node VARCHAR(16) NOT NULL, lan_id VARCHAR(4) NOT NULL, communication_net VARCHAR(8) NOT NULL, address VARCHAR(32) NOT NULL, confirmation_time TIMESTAMP NOT NULL);
CREATE INDEX INDEX_NODE_CONFIRMATIONS_CONCENTRATOR_NODE ON NODE_CONFIRMATIONS (concentrator, node);
CREATE UNIQUE INDEX INDEX_CONCENTRATIONS_NODE ON CONCENTRATIONS (node);
CREATE UNIQUE INDEX INDEX_CONCENTRATIONS_CONCENTRATOR_NODE ON CONCENTRATIONS (concentrator, node);
CREATE UNIQUE INDEX INDEX_CONCENTRATIONS_CONCENTRATOR_LAN_ID ON CONCENTRATIONS (concentrator, lan_id);