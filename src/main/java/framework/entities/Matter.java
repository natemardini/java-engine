package framework.entities;

import javax.persistence.Column;

public class Matter {

    public int id;

    public String name;

    @Column(name = "firm_id")
    public int firmId;
}
