package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.BaseObservable;
import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import java.util.ArrayList;
import java.util.List;

/*this class use the observer design pattern, the listeners are observers, if the FetchLastActiveQuestion
successfully fetches the questions from the FetchLastActiveQuestionEndPoint, it will notified all its
observers(listeners) with correct list of questions; if the FetchLastActiveQuestion fails to fetches
the questions from the FetchLastActiveQuestionEndPoint, then the observers(listeners) will be notified of failure */
public class FetchLastActiveQuestionsUseCase extends BaseObservable<FetchLastActiveQuestionsUseCase.Listener> {

    /*the class QuestionListController implements this interface, which means the class QuestionListController
    is one of the listener of the class FetchLastActiveQuestionUseCase, the controller will get notification if
    FetchLastActiveQuestionUseCase has any updates*/
    public interface Listener {
        void onLastActiveQuestionsFetched(List<Question> questions);
        void onLastActiveQuestionsFetchFailed();
    }

    private final FetchLastActiveQuestionsEndpoint mFetchLastActiveQuestionsEndpoint;

    public FetchLastActiveQuestionsUseCase(FetchLastActiveQuestionsEndpoint fetchLastActiveQuestionsEndpoint) {
        mFetchLastActiveQuestionsEndpoint = fetchLastActiveQuestionsEndpoint;
    }


    /*This is the core method in this Class, when this method is called, the questions in the server(
    FetchLastActiveQuestionEndPoint) will be fetched and passed to the controller(QuestionsListController),
    if the questions can not be fetched, the controller will be notified of failure*/
    public void fetchLastActiveQuestionsAndNotify() {
        mFetchLastActiveQuestionsEndpoint.fetchLastActiveQuestions(new FetchLastActiveQuestionsEndpoint.Listener() {
            @Override
            public void onQuestionsFetched(List<QuestionSchema> questions) {
                notifySuccess(questions);
            }

            @Override
            public void onQuestionsFetchFailed() {
                notifyFailure();
            }
        });
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onLastActiveQuestionsFetchFailed();
        }
    }

    private void notifySuccess(List<QuestionSchema> questionSchemas) {
        List<Question> questions = new ArrayList<>(questionSchemas.size());
        for (QuestionSchema questionSchema : questionSchemas) {
            questions.add(new Question(questionSchema.getId(), questionSchema.getTitle()));
        }
        for (Listener listener : getListeners()) {
            listener.onLastActiveQuestionsFetched(questions);
        }
    }
}
