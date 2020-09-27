package org.acme.repository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.acme.exception.BicycleNotFoundException;
import org.acme.model.Bicycle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class BicycleRepository {

    private final static Logger LOGGER = Logger.getLogger(BicycleRepository.class.getName());

    private final PgPool client;

    @Inject
    public BicycleRepository(PgPool client) {
        this.client = client;
    }

    public Multi<Bicycle> findAll() {
        return this.client
                .query("SELECT * FROM bicycle")
                .execute()
                .onItem().transformToMulti(
                        rs -> Multi.createFrom().items(() -> StreamSupport.stream(rs.spliterator(), false))
                )
                .map(this::rowToPost);

    }

    public Uni<Bicycle> findById(long id) {
        return this.client
                .preparedQuery("SELECT * FROM bicycle WHERE id=$1")
                .execute(Tuple.of(id))
                .map(RowSet::iterator)
                // .map(it -> it.hasNext() ? rowToPost(it.next()) : null);
                .flatMap(it -> it.hasNext() ? Uni.createFrom().item(rowToPost(it.next())) : Uni.createFrom().failure(BicycleNotFoundException::new));
    }

    public Uni<Integer> save(Bicycle data) {
        return this.client
                .preparedQuery("INSERT INTO bicycle (brand, name, type, gear, price) VALUES ($1, $2, $3, $4, $5) RETURNING (id) ")
                .execute(Tuple.of(data.getBrand(), data.getName(), data.getType(), data.getGear(), data.getPrice()))
                .map(RowSet::iterator)
                .map(it -> it.hasNext() ? it.next().getInteger("id") : null);
    }

    public Uni<Integer> update(long id, Bicycle data) {
        return this.client
                .preparedQuery("UPDATE bicycle SET brand=$1, name=$2, type=$3, gear=$4, price=$5 WHERE id=$6")
                .execute(Tuple.of(data.getBrand(), data.getName(), data.getType(), data.getGear(), data.getPrice(), id))
                .map(RowSet::rowCount);
    }

    public Uni<Integer> deleteAll() {
        return client.query("DELETE FROM bicycle")
                .execute()
                .map(RowSet::rowCount);
    }

    public Uni<Integer> delete(Long id) {
        return client.preparedQuery("DELETE FROM bicycle WHERE id=$1")
                .execute(Tuple.of(id))
                .map(RowSet::rowCount);
    }

    private Bicycle rowToPost(Row row) {
        return Bicycle.of(row.getInteger("id"), row.getString("brand"), row.getString("name"), row.getString("type"), row.getInteger("gear"), row.getDouble("price"));
    }

}
