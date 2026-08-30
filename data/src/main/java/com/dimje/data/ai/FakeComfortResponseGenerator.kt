package com.dimje.data.ai

import com.dimje.domain.repository.ComfortResponseGenerator
import javax.inject.Inject
import kotlinx.coroutines.delay

class FakeComfortResponseGenerator @Inject constructor() : ComfortResponseGenerator {
    override suspend fun generate(worry: String): String {
        delay(700)

        if (riskKeywords.any(worry::contains)) {
            return "지금 이 마음을 혼자 견디고 있다는 사실이 걱정돼요. " +
                "당장 자신을 다치게 할 가능성이 있다면 혼자 있지 말고 가까운 사람에게 알린 뒤 " +
                "112 또는 119 같은 긴급 도움을 요청해 주세요. 이 앱의 답변보다 당신의 안전이 가장 중요해요."
        }

        val reassurance = when {
            workKeywords.any(worry::contains) ->
                "잘해내고 싶은 마음이 큰 만큼 오늘의 부담도 무겁게 느껴졌을 것 같아요."
            relationshipKeywords.any(worry::contains) ->
                "관계에서 생긴 마음은 혼자 정리하기 어렵고 오래 남기도 해요."
            futureKeywords.any(worry::contains) ->
                "아직 오지 않은 일을 생각하느라 마음이 먼저 먼 길을 다녀온 것 같아요."
            else ->
                "말로 꺼내기까지 마음속에서 여러 번 되뇌었을 생각을 들려줘서 고마워요."
        }

        return "$reassurance 오늘 밤에 모든 답을 찾지 않아도 괜찮아요. " +
            "지금은 천천히 숨을 고르고, 해결해야 할 일은 내일의 나에게 잠시 맡겨 두어요. " +
            "오늘도 충분히 애썼어요. 편안한 밤이 되기를 바랄게요."
    }

    private companion object {
        val riskKeywords = setOf("죽고 싶", "사라지고 싶", "자해", "극단적", "목숨")
        val workKeywords = setOf("회사", "직장", "업무", "시험", "공부", "취업", "실수")
        val relationshipKeywords = setOf("친구", "가족", "연인", "관계", "외로", "연락")
        val futureKeywords = setOf("미래", "앞으로", "진로", "선택", "결정")
    }
}
