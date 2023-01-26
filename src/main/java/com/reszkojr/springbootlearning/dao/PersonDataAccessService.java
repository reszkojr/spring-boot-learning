package com.reszkojr.springbootlearning.dao;

import com.reszkojr.springbootlearning.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres")
public class PersonDataAccessService implements PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UUID insertPerson(UUID id, Person person) {
        final String sql = "INSERT INTO person (name, id) VALUES (?, ?)";

        jdbcTemplate.update(sql, person.getName(), id);

        return id;
    }

    @Override
    public List<Person> selectAllPeople() {
        final String sql = "SELECT * FROM person";
        return jdbcTemplate.query(sql, (rs, i) -> {
            UUID id = UUID.fromString(rs.getString("id"));
            String name = rs.getString("name");
            return new Person(id, name);
        });
    }

    @Override
    public Optional<Person> selectPersonById(UUID id) {
        final String sql = "SELECT name, id FROM person where id = ?";
        Person person = jdbcTemplate.queryForObject(sql, (rs, i) -> {
            String name = rs.getString("name");
            return new Person(id, name);
        }, id);
        return Optional.ofNullable(person);
    }

    @Override
    public int deletePersonById(UUID id) {
        Person delPerson = selectPersonById(id).orElse(null);
        if (delPerson == null) return 1;

        final String sql = "DELETE FROM person where id = ?";
        jdbcTemplate.update(sql, id);

        return 0;
    }

    @Override
    public int updatePersonById(UUID id, Person person) {
        Person delPerson = selectPersonById(id).orElse(null);
        if (delPerson == null) return 1;

        final String sql = "UPDATE person SET name = ? where id = ?";

        jdbcTemplate.update(sql, person.getName(), id);

        return 0;
    }
}

