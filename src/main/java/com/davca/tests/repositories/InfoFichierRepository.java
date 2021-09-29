/**
 * 
 */
package com.davca.tests.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.davca.tests.entities.InfoFichier;

/**
 * InfoFichierRepository
 * 
 * Spring data repository for InfoFichiers
 * 
 * @author KONGNUY Victorien on 28-09-2021
 *
 */
@Repository
public interface InfoFichierRepository extends CrudRepository<InfoFichier, Long> {

  InfoFichier findByPath(String stringCellValue);

  Iterable<InfoFichier> findByType(String string);

  Iterable<InfoFichier> findByTypeOrTypeOrTypeOrType(String string, String string2, String string3, String string4);

}
