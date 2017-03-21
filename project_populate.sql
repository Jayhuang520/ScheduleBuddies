SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


DROP SCHEMA IF EXISTS `Project` ;

CREATE SCHEMA IF NOT EXISTS `Project` DEFAULT CHARACTER SET latin1 ;
USE `Project` ;

-- -----------------------------------------------------
-- Table `Project`.`user_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Project`.`user_info` (
  `user_id` CHAR(30) NOT NULL,
  `first_name` CHAR(30) NULL DEFAULT NULL,
  `last_name` CHAR(30) NULL DEFAULT NULL,
  `email` CHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `Project`.`geolocation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Project`.`geolocation` (
  `longitude` FLOAT NOT NULL,
  `latitude` FLOAT NOT NULL,
  PRIMARY KEY (`longitude`, `latitude`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `Project`.`events`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Project`.`events` (
  `event_id` CHAR(30) NOT NULL,
  `user_id` CHAR(30) NULL DEFAULT NULL,
  `name` CHAR(40) NULL DEFAULT NULL,
  `category` CHAR(30) NULL DEFAULT NULL,
  `latitude` FLOAT NOT NULL,
  `longitude` FLOAT NOT NULL,
  `user_info_user_id` CHAR(30) NOT NULL,
  `geolocation_longitude` FLOAT NOT NULL,
  `geolocation_latitude` FLOAT NOT NULL,
  PRIMARY KEY (`event_id`, `latitude`, `longitude`),
  INDEX `fk_events_user_info_idx` (`user_info_user_id` ASC),
  INDEX `fk_events_geolocation1_idx` (`geolocation_longitude` ASC, `geolocation_latitude` ASC),
  CONSTRAINT `fk_events_user_info`
    FOREIGN KEY (`user_info_user_id`)
    REFERENCES `Project`.`user_info` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_events_geolocation1`
    FOREIGN KEY (`geolocation_longitude` , `geolocation_latitude`)
    REFERENCES `Project`.`geolocation` (`longitude` , `latitude`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `Project`.`saved_events`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Project`.`saved_events` (
  `save_event_id` CHAR(30) NOT NULL,
  `user_id` CHAR(30) NULL DEFAULT NULL,
  `name` CHAR(40) NULL DEFAULT NULL,
  `category` CHAR(30) NULL DEFAULT NULL,
  `events_event_id` CHAR(30) NOT NULL,
  `user_info_user_id` CHAR(30) NOT NULL,
  PRIMARY KEY (`save_event_id`, `user_info_user_id`),
  INDEX `fk_saved_events_events1_idx` (`events_event_id` ASC),
  INDEX `fk_saved_events_user_info1_idx` (`user_info_user_id` ASC),
  CONSTRAINT `fk_saved_events_events1`
    FOREIGN KEY (`events_event_id`)
    REFERENCES `Project`.`events` (`event_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_saved_events_user_info1`
    FOREIGN KEY (`user_info_user_id`)
    REFERENCES `Project`.`user_info` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

