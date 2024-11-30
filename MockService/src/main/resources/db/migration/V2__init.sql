CREATE TABLE face
(
    id        UUID NOT NULL,
    face_data OID  NOT NULL,
    CONSTRAINT pk_face PRIMARY KEY (id)
);