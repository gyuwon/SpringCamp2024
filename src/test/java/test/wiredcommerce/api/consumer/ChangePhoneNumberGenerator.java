package test.wiredcommerce.api.consumer;

import java.util.concurrent.ThreadLocalRandom;

import autoparams.ObjectQuery;
import autoparams.ResolutionContext;
import autoparams.generator.ObjectGeneratorBase;
import wiredcommerce.consumer.command.ChangePhoneNumber;

public class ChangePhoneNumberGenerator extends ObjectGeneratorBase<ChangePhoneNumber> {

    @Override
    protected ChangePhoneNumber generateObject(ObjectQuery query, ResolutionContext context) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String phoneNumber = "010-"
            + random.nextInt(1000, 10000) + "-"
            + random.nextInt(1000, 10000);
        return new ChangePhoneNumber(phoneNumber);
    }
}
