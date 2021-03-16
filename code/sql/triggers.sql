CREATE SEQUENCE cnum_seq START WITH 2000;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION func_cruise()
        RETURNS "trigger" AS
        $BODY$
        BEGIN

        New.cnum:=nextval('cnum_seq');
        RETURN NEW;
        END
        $BODY$
        LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT ON Cruise
FOR EACH ROW
EXECUTE PROCEDURE func_cruise();

---------------------------------------------------

CREATE SEQUENCE customer_seq START WITH 250;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION func_customer()
        RETURNS "trigger" AS
        $BODY$
        BEGIN

        New.id:=nextval('customer_seq');
        RETURN NEW;
        END
        $BODY$
        LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT ON Customer
FOR EACH ROW
EXECUTE PROCEDURE func_customer();

---------------------------------------------------

CREATE SEQUENCE reservation_seq START WITH 9999;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION func_reservation()
        RETURNS "trigger" AS
        $BODY$
        BEGIN

        New.rnum:=nextval('reservation_seq');
        RETURN NEW;
        END
        $BODY$
        LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT ON Reservation
FOR EACH ROW
EXECUTE PROCEDURE func_reservation();

---------------------------------------------------

CREATE SEQUENCE ship_seq START WITH 67;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION func_ship()
        RETURNS "trigger" AS
        $BODY$
        BEGIN

        New.id:=nextval('ship_seq');
        RETURN NEW;
        END
        $BODY$
        LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT ON Ship
FOR EACH ROW
EXECUTE PROCEDURE func_ship();

----------------------------------------------------

CREATE SEQUENCE captain_seq START WITH 250;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION func_captain()
        RETURNS "trigger" AS
        $BODY$
        BEGIN

        New.id:=nextval('captain_seq');
        RETURN NEW;
        END
        $BODY$
        LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT ON Captain
FOR EACH ROW
EXECUTE PROCEDURE func_captain();


