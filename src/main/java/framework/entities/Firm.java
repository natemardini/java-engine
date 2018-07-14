package framework.entities;

import framework.config.Database;
import org.jooq.DSLContext;

import java.util.List;

import static framework.entities.jooq.Tables.*;

public class Firm {

    public int id;
    public String name;

    public static Firm getOne(int lookupId) {
        DSLContext ctx = Database.getContext();

        return ctx.select()
                .from(FIRMS)
                .where(FIRMS.ID.eq(lookupId))
                .fetchAny()
                .into(Firm.class);
    }

    public List<Matter> getMatters() {
        DSLContext ctx = Database.getContext();

        return ctx.select()
                .from(MATTERS)
                .where(MATTERS.FIRM_ID.eq(id))
                .fetch()
                .into(Matter.class);
    }
}
