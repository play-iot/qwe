package io.zero88.qwe;

import java.util.Collection;

interface ContextLookupInternal extends ContextLookup {

    Collection<ComponentContext> list();

    void add(ComponentContext context);

}
