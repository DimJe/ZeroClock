package com.dimje.domain.usecase

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.WorryAnalysis
import com.dimje.domain.model.WorryEntry

class AnalyzeWorriesUseCase(
    private val flowLogger: DataFlowLogger = DataFlowLogger.NONE,
) {
    operator fun invoke(entries: List<WorryEntry>): WorryAnalysis? {
        val uniqueEntries = entries.distinctBy { it.date }
        flowLogger.log("DOMAIN", "고민 분석 요청 수신", "uniqueEntryCount=${uniqueEntries.size}")
        if (uniqueEntries.size < MINIMUM_ENTRY_COUNT) {
            flowLogger.log("DOMAIN", "고민 분석 결과 전달", "available=false")
            return null
        }

        val texts = uniqueEntries.map { it.worry }
        val concern = concernRules
            .map { rule -> rule to texts.sumOf { text -> rule.keywords.count(text::contains) } }
            .maxByOrNull { it.second }
            ?.takeIf { it.second > 0 }
            ?.first
            ?: generalConcern

        val keywords = texts
            .flatMap { tokenPattern.findAll(it.lowercase()).map(MatchResult::value) }
            .filterNot(stopWords::contains)
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(KEYWORD_COUNT)
            .map { it.key }

        return WorryAnalysis(
            entryCount = uniqueEntries.size,
            mainConcern = concern.title,
            keywords = keywords,
            suggestion = concern.suggestion,
        ).also { analysis ->
            flowLogger.log(
                "DOMAIN",
                "고민 분석 결과 전달",
                "available=true, entryCount=${analysis.entryCount}, keywordCount=${analysis.keywords.size}",
            )
        }
    }

    private data class ConcernRule(
        val title: String,
        val keywords: Set<String>,
        val suggestion: String,
    )

    private companion object {
        const val MINIMUM_ENTRY_COUNT = 15
        const val KEYWORD_COUNT = 5
        val tokenPattern = Regex("[가-힣a-zA-Z0-9]{2,}")
        val stopWords = setOf(
            "오늘", "내일", "정말", "너무", "조금", "계속", "자꾸", "같아", "것이", "나는", "내가",
            "그리고", "하지만", "때문", "걱정", "고민", "불안", "있어", "없어", "합니다", "해요",
        )
        val concernRules = listOf(
            ConcernRule(
                "일과 성취에 대한 부담",
                setOf("회사", "직장", "업무", "일", "시험", "공부", "성적", "취업", "실수"),
                "내일 해야 할 일을 가장 작은 한 단계로 나누어 적어 보세요. 오늘 밤에는 해결보다 휴식을 우선해도 괜찮습니다.",
            ),
            ConcernRule(
                "관계에서 오는 마음의 피로",
                setOf("친구", "가족", "연인", "사람", "관계", "연락", "말", "외로"),
                "상대의 마음을 추측하기보다 내 감정을 한 문장으로 적어 보세요. 대화는 마음이 조금 가라앉은 뒤 시작해도 늦지 않습니다.",
            ),
            ConcernRule(
                "미래와 변화에 대한 불안",
                setOf("미래", "앞으로", "진로", "선택", "변화", "결정", "계획"),
                "통제할 수 있는 일과 없는 일을 나눈 뒤, 내일 할 수 있는 한 가지에만 표시해 보세요.",
            ),
            ConcernRule(
                "건강과 회복에 대한 염려",
                setOf("건강", "아프", "병원", "잠", "수면", "피곤", "몸", "마음"),
                "지금은 화면을 잠시 내려놓고 천천히 호흡해 보세요. 불편이 지속되면 혼자 판단하지 말고 전문가와 상의하세요.",
            ),
            ConcernRule(
                "경제적인 부담",
                setOf("돈", "생활비", "월급", "대출", "비용", "경제", "저축"),
                "전체 문제를 한꺼번에 해결하려 하지 말고, 이번 주에 확인할 수입과 지출 한 항목부터 적어 보세요.",
            ),
        )
        val generalConcern = ConcernRule(
            "여러 걱정이 겹쳐 생긴 마음의 피로",
            emptySet(),
            "가장 마음에 걸리는 생각 하나만 짧게 적고, 오늘 해결하지 않아도 되는 일은 내일의 나에게 잠시 맡겨 보세요.",
        )
    }
}
