package com.yrgo.services.calls;

import com.yrgo.domain.Action;
import com.yrgo.domain.Call;
import com.yrgo.services.customers.CustomerManagementService;
import com.yrgo.services.customers.CustomerNotFoundException;
import com.yrgo.services.diary.DiaryManagementService;

import java.util.Collection;

public class CallHandlingServiceImpl implements CallHandlingService {

    private CustomerManagementService customerService;
    private DiaryManagementService diaryService;

    public CallHandlingServiceImpl (CustomerManagementService customerService, DiaryManagementService diaryService) {
        this.customerService = customerService;
        this.diaryService = diaryService;
    }

/*    public void setCustomerService(CustomerManagementService customerService) {
        this.customerService = customerService;
    }

    public void setDiaryService(DiaryManagementService diaryService) {
        this.diaryService = diaryService;
    }*/

    @Override
    public void recordCall(String customerId, Call newCall, Collection<Action> actions) throws CustomerNotFoundException {

        customerService.recordCall(customerId, newCall);
        actions.forEach(action -> diaryService.recordAction(action));
        // Eller, med metodreferens:
        // actions.forEach(diaryService::recordAction);




    }
}
