after pull the code.
compile and run , it will create user ,role table but user_roles table will create manually.
inerst the role into the role table.


CREATE TABLE app_users1 (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
      FOREIGN KEY (user_id)
      REFERENCES app_users1(id)
      ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
      FOREIGN KEY (role_id)
      REFERENCES roles(id)
);


INSERT INTO roles(name)
VALUES ('ADMIN'), ('OFFICER'), ('VIEWER')
ON CONFLICT (name) DO NOTHING;
After registration the user database administrator will provide perssion like ,admin,officer,viewer and also set enable in user registration table.
UPDATE app_users
SET enabled = TRUE
