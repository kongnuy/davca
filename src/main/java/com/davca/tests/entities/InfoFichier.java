/**
 * 
 */
package com.davca.tests.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * InfoFichier
 * 
 * @author KONGNUY Victorien on 28-09-2021
 * 
 */
@Entity
@Table(name = "info_fichiers")
@NoArgsConstructor
public class InfoFichier implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  @Getter
  @Setter
  String drive;

  @Getter
  @Setter
  String filename;

  @Getter
  @Setter
  String folder;

  @Getter
  @Setter
  String path;

  @Getter
  @Setter
  Double size;

  @Getter
  @Setter
  String type;

  @Getter
  @Setter
  Date lastAccess;

  @Getter
  @Setter
  Date createdAt;

  @Getter
  @Setter
  Date updatedAt;

}