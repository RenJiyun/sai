--changeset ren:1.1
CREATE TABLE [user]
(
    [id] int NOT NULL IDENTITY(1,1),
    [user_no] varchar(30) NULL,
    [user_name] varchar(50) NULL,
    [phone] varchar(20) NULL,
    [open_id] varchar (30) NULL,
    [password] varchar(50) NULL,
    [create_time] datetime NULL DEFAULT getdate(),
    [edit_time] datetime NULL,
    PRIMARY KEY ([id])
)