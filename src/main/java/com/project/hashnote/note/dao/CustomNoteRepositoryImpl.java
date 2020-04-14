package com.project.hashnote.note.dao;

import com.project.hashnote.note.document.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class CustomNoteRepositoryImpl implements CustomNoteRepository {
    private MongoOperations mongoOperations;

    @Autowired
    public CustomNoteRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<Note> findAllLimitedNotes() {
        ProjectionOperation projectionOperation = Aggregation.project()
                .and(ComparisonOperators.Cmp.valueOf("maxVisits").compareToValue(0)).as("isMaxVisitsSet")
                .and(ComparisonOperators.Cmp.valueOf("keyVisits").compareTo("maxVisits")).as("visitsComparison")
                .and("$$ROOT").as("obj");

        MatchOperation matchLimitedNotes = Aggregation.match(new Criteria("isMaxVisitsSet").gt(0));
        MatchOperation matchLimitExceededNotes = Aggregation.match(new Criteria("visitsComparison").gte(0));

        ReplaceRootOperation replaceRootOperation = ReplaceRootOperation.builder().withValueOf("obj");

        Aggregation aggregation2 = Aggregation.newAggregation(
                projectionOperation,
                matchLimitedNotes,
                matchLimitExceededNotes,
                replaceRootOperation
        );

        AggregationResults<Note> results = mongoOperations.aggregate(aggregation2, "notes", Note.class);

        return results.getMappedResults();
    }
}
