package com.wyz.emlibrary.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.setPadding
import com.wyz.emlibrary.R
import kotlin.math.*

/**
 * app:background_Type
 * 它有三种不同的值：
 *
 * fill（默认）：视图仅有填充颜色。
 * stroke：视图仅有描边颜色。
 * fill_stroke：视图具有填充颜色和描边颜色。
 * 注意：此属性严格定义视图颜色行为。当您定义background_Type为填充时，无论您定义描边颜色还是宽度，它都会被忽略。
 *
 * app:stroke_Width
 * 如果选择有描边，则为描边的宽度，默认宽度为1dp。
 *
 * app:stroke_Type
 * 它的工作原理与 相同background_Type，并将定义笔划的类型。它有两个不同的值：
 *
 * solid（默认）：视图描边是实线。
 * dash：视图笔划为虚线。
 * 注意：此属性严格定义了笔划类型。
 *
 * app:[background/stroke]_ColorType
 * 如果要在背景或描边上实现渐变，则必须定义它们的ColorTypeapp:background_ColorType 。我们有背景和app:stroke_ColorType描边的属性。两者都有这些值（除了描边不支持gradient_radial ）：
 *
 * solid（默认）：颜色是纯色。
 * gradient_linear：颜色为线性渐变。
 * gradient_radial：颜色为径向渐变（Stroke 不支持此项）。
 * gradient_sweep：颜色为sweep_gradient。
 * 警告：这些属性严格定义颜色类型。
 *
 * app:[background/stroke]_Color和app:[background/stroke]_Alpha
 * 如果将solid定义为背景/描边颜色类型，则背景/描边的颜色将由属性决定app:[background/stroke]_Color。
 *
 * 当然我们有背景/描边的 alpha：app:[background/stroke]_Alpha
 *
 * 请记住： app:[background/stroke]_Alpha也会影响渐变。 注意：如果您在颜色十六进制中定义了 alpha，则库将乘以app:[background/stroke]_Alpha您定义的颜色十六进制 alpha。所以请记住这一点。
 *
 * app:[background/stroke]_Gradient_Color[index/End]
 * 如果您选择一种渐变方法作为背景/描边颜色类型，则可以定义最多 8 种颜色作为渐变颜色。定义渐变颜色的属性包括：
 *
 * app:[background/stroke]_Gradient_Color0
 * app:[background/stroke]_Gradient_Color1
 * app:[background/stroke]_Gradient_Color2
 * app:[background/stroke]_Gradient_Color3
 * app:[background/stroke]_Gradient_Color4
 * app:[background/stroke]_Gradient_Color5
 * app:[background/stroke]_Gradient_Color6
 * app:[background/stroke]_Gradient_ColorEnd
 * 您不必定义所有这些 xml 属性，只需按索引顺序定义渐变所需的颜色数量，并将最后一种颜色定义为app:[background/stroke]_Gradient_ColorEnd。 注意：您必须定义渐变颜色的ColorEnd，否则库将假定Color0为渐变的结束颜色。
 *
 * 如果以编程方式定义渐变颜色，则可以在渐变上有无限的颜色：
 *
 * acd.[background/stroke]_Gradient_Colors = colorArray
 * app:[background/stroke_Gradient_Angle
 * 顾名思义，您可以更改渐变角度。默认角度为0，表示上下颠倒。角度范围为 0 到 360，顺时针旋转。
 *
 * app:[background/stroke]_Gradient_OffCenter_[X/Y]
 * 在gradient_radial和gradient_sweep中，渐变的中心就是视图的中心。您可以使用OffCenter_X或OffCenter_Y乘数更改渐变中心。这些乘数的范围从-1到+1，默认值为0，表示不移动中心。
 *
 * app:stroke_DashSize，app:stroke_GapSize和app:stroke_CapType
 * 如果您选择使用虚线类型的笔触，则可以定义这些属性来更改虚线行为。
 * app:cornerRadius，app:cornerRadius_TopLeft，app:cornerRadius_TopRight，app:cornerRadius_BottomRight和app:cornerRadius_BottomLeft
 * 这些属性定义角半径。app:cornerRadius将同时改变所有角，但您也可以定义其他特定的角。
 * 要点：优先app:cornerRadius级较低，这意味着如果您定义此属性并具体定义其他角半径，如app:cornerRadius_TopLeft，左上角半径将不再由 决定app:cornerRadius。因此，如果您希望一个角与其他角不同，您可以定义cornerRadius并具体告诉库您希望哪个角不同，
 *
 * app:corner_Type
 * 它将根据以下值自动计算角半径：
 *
 * custom（默认）：图书馆将采用您的角半径。
 * rectangular：无论你是否定义相关属性，它都会将角半径设置为0dp。
 * circular：它将使视图的边缘变成圆形。如果您的视图宽度和高度相等（正方形），它将使它变成圆形。在此方法中，库计算视图的最小边缘长度，并将角半径设置为其一半以使其变成圆形。
 * third：它使角弯曲，但张力比 更小circular。库计算视图的最小边长，并将角半径设置为其三分之一。
 * quarter：与第三种方法相同，但库将角半径设置为最小边长的四分之一。
 *
 * app:shadow[index]_[Inner/Outer]_[Blur/Distance/Alpha/Color/Angle]
 * 最后，我们有定义阴影的属性。因此，如果我们想通过命名对流来定义第一个内阴影的模糊，我们将有这样的属性：app:shadow0_Inner_Blur。阴影属性如下所述：
 *
 * Blur：定义阴影的模糊程度。
 * Distance：定义阴影的距离。
 * Alpha：定义阴影的 alpha 值。
 * Color：定义阴影的颜色。
 * Angle：定义阴影的角度。
 * 记住：角度行为与我们之前讨论的渐变角度相同。 注意： Blur必须定义属性，否则库不会绘制相关阴影。
 *
 * app:shadow_Outer_Area以及库如何绘制外阴影（阴影）
 * 从 API 26 开始，Android 框架不允许视图在其边界之外绘制。为了解决这个问题，外阴影是视图边界的一部分。这意味着如果我们绘制外阴影，我们就会有插入或填充，如下图所示。该app:shadow_Outer_Area参数定义了外阴影需要多少插入。一旦您声明第一个外阴影，此参数的默认值将为8dp，否则我们没有任何插入。app:shadow_Outer_Area定义后，库会自动设置填充，以便子视图不会不自然地剪切
 * 要点：大多数情况下8dp内边距就足够了，这意味着如果外阴影的距离小于 8dp，视图边界将不会剪切外阴影。但如果外阴影的距离大于 8dp，请适当更改此属性。 请记住：此值仅对外阴影有效​​，对内阴影无效。 注意：让库自动设置内边距，不要手动设置内边距。您可以使用margin它来调整视图子项的位置。
 *
 * https://github.com/sina-seyfi/advancedcardview
 *
 * <com.mcbattery.manager.saver.phone.ui.custom.AdvancedCardView
 *         android:layout_width="match_parent"
 *         android:layout_height="100dp"
 *         app:background_Type="fill_stroke"
 *         android:layout_alignParentBottom="true"
 *         app:cornerRadius="20dp"
 *         app:shadow0_Inner_Blur="8dp"
 *         app:shadow0_Inner_Color="@color/power_monitor_shadow_color2"
 *         app:shadow0_Outer_Blur="16dp"
 *         app:shadow0_Outer_Color="@color/power_monitor_shadow_color3"
 *         app:shadow_Outer_Area="16dp"
 *
 *
 *         app:background_ColorType="gradient_radial"
 *         app:background_Gradient_Color0="@color/chart_y_axis_text_color"
 *         app:background_Gradient_Color1="@color/chart_y_axis_text_color"
 *         app:background_Gradient_Color2="@color/battery_item_cold_color"
 *         app:background_Gradient_Color3="@color/battery_item_healthy_color"
 *         app:background_Gradient_Color4="@color/battery_endurance_end_color"
 *
 *         app:stroke_Width="5dp"
 *         app:stroke_ColorType="solid"
 *         app:stroke_Gradient_Angle="0"
 *         app:stroke_Gradient_Color0="@color/chart_y_axis_text_color"
 *         app:stroke_Gradient_Color1="@color/battery_item_cold_color"
 *         app:stroke_Gradient_Color2="@color/battery_item_healthy_color"
 *         app:stroke_Gradient_Color3="@color/battery_endurance_end_color" />
 */
class AdvancedCardView: FrameLayout {

    companion object {
        const val TAG = "ADVANCED_CARD_VIEW"
        val DEFAULT_BACKGROUND_COLOR_TYPE = ColorType.Solid
        val DEFAULT_BACKGROUND_TYPE = BackgroundType.Fill
        val DEFAULT_COLOR_BACKGROUND = Color.rgb( 255, 255, 255)
        val DEFAULT_COLOR_STROKE = Color.rgb(128, 128, 128)
        val DEFAULT_CORNER_RADIUS = 0f
        val DEFAULT_CORNER_TYPE = CornerType.Custom
        val DEFAULT_OFFCENTER_X = 0f
        val DEFAULT_OFFCENTER_Y = 0f
        val DEFAULT_RADIUS_MULTIPLIER = 1f
        val DEFAULT_SHADOW_COLOR = Color.rgb(0, 0, 0)
        val DEFAULT_STROKE_CAP_TYPE = CapType.Butt
        val DEFAULT_STROKE_COLOR_TYPE = ColorType.Solid
        val DEFAULT_STROKE_SIZE = 0f
        val DEFAULT_STROKE_TYPE = StrokeType.Solid
        val NOT_DEFINED_ALPHA = 1f
        val NOT_DEFINED_ANGLE = 0f
        val NOT_DEFINED_COLOR = -10
        val NOT_DEFINED_CORNER_RADIUS = Float.MIN_VALUE
        val NOT_DEFINED_DIMEN = -1f
        val NOT_DEFINED_RADIUS = -1f
        val NOT_DEFINED_SHADOW_OUTER_AREA = -1f
//        val NOT_DEFINED_SOURCE = -1       // TODO Implement Background Image Support
    }

    // Constants that requires "resources"
    private val DEFAULT_SHADOW_OUTER_AREA = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
    private val DEFAULT_STROKE_DASH_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
    private val DEFAULT_STROKE_GAP_SIZE = DEFAULT_STROKE_DASH_SIZE
    private val SHADOW_OUTER_MASK_ERROR = 0.5f

    enum class BackgroundType {
        Fill,
        Stroke,
        Fill_Stroke
    }

    enum class StrokeType {
        Solid,
        Dash
    }

    enum class ColorType {
        Solid,
        Gradient_Linear,
        Gradient_Radial,
        Gradient_Sweep
    }

    enum class CapType {
        Square,
        Butt,
        Round
    }

    enum class CornerType {
        Custom,
        Rectangular,
        Circular,
        Third,
        Quarter
    }

    enum class ShadowType {
        Outer,
        Inner
    }

    private val DEBUG_MODE = false
    private val DEBUG_fill_color = Color.rgb(128, 128, 128)
    private val DEBUG_stroke_color = Color.rgb(0, 0, 255)
    private val DEBUG_stroke_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    private val DEBUG_paint_fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val DEBUG_paint_stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private val DEBUG_path = Path()

    // Background Fields
    var background_Type: BackgroundType = DEFAULT_BACKGROUND_TYPE
    var background_ColorType: ColorType = DEFAULT_BACKGROUND_COLOR_TYPE
    var background_Color: Int = DEFAULT_COLOR_BACKGROUND
    //    var background_Src = NOT_DEFINED_SOURCE
    private var background_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var background_Path = Path()
    var background_Alpha: Float = NOT_DEFINED_ALPHA
        set(value) {
            field = floatPercentCheck(value)
        }
    private lateinit var background_Path_Radii: FloatArray
    private var background_Path_Radii_Updated = false

    // Background Gradient Fields
    private var background_Gradient_Colors_Xml = IntArray(8, { NOT_DEFINED_COLOR })
    lateinit var background_Gradient_Colors: IntArray
    var background_Gradient_Angle: Float = NOT_DEFINED_ANGLE
        set(value) {
            field = angleCheck(value)
        }
    var background_Gradient_OffCenter_X = DEFAULT_OFFCENTER_X
        set(value) {
            field = boundaryCheck(value, 1f)
        }
    var background_Gradient_OffCenter_Y = DEFAULT_OFFCENTER_Y
        set(value) {
            field = boundaryCheck(value, 1f)
        }
    // TODO Implement Radius Multiplier
//    private var background_Gradient_Radius = NOT_DEFINED_RADIUS
//    private var background_Gradient_Radius_Multiplier = DEFAULT_RADIUS_MULTIPLIER

    // Stroke Fields
    var stroke_Type: StrokeType = DEFAULT_STROKE_TYPE
    var stroke_ColorType: ColorType = DEFAULT_STROKE_COLOR_TYPE
    var stroke_Color: Int = DEFAULT_COLOR_STROKE
    private var stroke_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var stroke_Mask = Path()
    private var stroke_Path = Path()
    private var no_Stroke_Path = Path()
    var stroke_Alpha: Float = NOT_DEFINED_ALPHA
        set(value) {
            field = floatPercentCheck(value)
        }
    private lateinit var stroke_Mask_Radii: FloatArray
    private var stroke_Mask_Radii_Updated = false
    private lateinit var stroke_Path_Radii: FloatArray
    private var stroke_Path_Radii_Updated = false

    var stroke_Width: Float = DEFAULT_STROKE_SIZE
        set(value) {
            field = dimenCheck(value, null)
        }
    var stroke_DashSize: Float = DEFAULT_STROKE_DASH_SIZE
        set(value) {
            field = dimenCheck(value, null)
        }
    var stroke_GapSize: Float = DEFAULT_STROKE_GAP_SIZE
        set(value) {
            field = dimenCheck(value, null)
        }
    var stroke_CapType: CapType = DEFAULT_STROKE_CAP_TYPE

    // Stroke Gradient Fields
    private var stroke_Gradient_Colors_Xml = IntArray(8, { NOT_DEFINED_COLOR })
    lateinit var stroke_Gradient_Colors: IntArray
    var stroke_Gradient_Angle = NOT_DEFINED_ANGLE
        set(value) {
            field = angleCheck(value)
        }

    var stroke_Gradient_OffCenter_X: Float = DEFAULT_OFFCENTER_X
        set(value) {
            field = boundaryCheck(value, 1f)
        }
    var stroke_Gradient_OffCenter_Y: Float = DEFAULT_OFFCENTER_Y
        set(value) {
            field = boundaryCheck(value, 1f)
        }

    private class ShadowObject {
        var shadowType: ShadowType
        var color = DEFAULT_SHADOW_COLOR
        var alpha = NOT_DEFINED_ALPHA
        var distance = NOT_DEFINED_DIMEN
        var blur = NOT_DEFINED_DIMEN
        var angle = NOT_DEFINED_ANGLE
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var path = Path()
        var mask = Path()
        constructor(shadowType: Int) {
            this.shadowType = ShadowType.values()[shadowType]
        }
    }

    // First Two Are Outer Shadows, And Last Two Are Inner Shadows
    private var shadows = Array<ShadowObject>(4) { ShadowObject((it / 2))}

    private lateinit var innerShadow_External_Path_Radii: FloatArray
    var shadow_Outer_Area: Float = NOT_DEFINED_SHADOW_OUTER_AREA
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_SHADOW_OUTER_AREA)
            initPadding()
        }

    // Corner Fields
    var cornerRadius_: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_TopLeft: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_TopRight: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_BottomLeft: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_BottomRight: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerType = DEFAULT_CORNER_TYPE
        set(value) {
            field = value
            cornerUpdated()
        }

    constructor(context: Context) : super(context) { initialize(context, null) }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize(context, attrs) }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize(context, attrs) }

    // Preparing Area

    private fun initialize(context: Context, attrs: AttributeSet?) {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AdvancedCardView)
            // Get Background Style from XML
            background_Type = BackgroundType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_background_Type, DEFAULT_BACKGROUND_TYPE.ordinal)]
            background_ColorType = ColorType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_background_ColorType, DEFAULT_BACKGROUND_COLOR_TYPE.ordinal)]
            background_Color = typedArray.getColor(R.styleable.AdvancedCardView_background_Color, DEFAULT_COLOR_BACKGROUND)
            background_Alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Alpha, NOT_DEFINED_ALPHA))
            background_Gradient_Colors_Xml[0] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color0, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[1] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color1, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[2] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color2, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[3] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color3, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[4] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color4, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[5] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color5, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[6] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color6, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[7] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_ColorEnd, NOT_DEFINED_COLOR)
            background_Gradient_Angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_Angle, NOT_DEFINED_ANGLE))
            background_Gradient_OffCenter_X = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_OffCenter_X, DEFAULT_OFFCENTER_X), 1f)
            background_Gradient_OffCenter_Y = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_OffCenter_Y, DEFAULT_OFFCENTER_Y), 1f)
//            background_Gradient_Radius = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_background_Gradient_Radius, NOT_DEFINED_RADIUS))
//            background_Gradient_Radius_Multiplier = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_Radius_Multiplier, DEFAULT_RADIUS_MULTIPLIER))
            // Get Stroke Style from XML
            stroke_Type = StrokeType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_stroke_Type, DEFAULT_STROKE_TYPE.ordinal)]
            stroke_ColorType = ColorType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_stroke_ColorType, DEFAULT_STROKE_COLOR_TYPE.ordinal)]
            stroke_Color = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Color, DEFAULT_COLOR_STROKE)
            stroke_Alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Alpha, NOT_DEFINED_ALPHA))
            stroke_Width = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_stroke_Width, DEFAULT_STROKE_SIZE), null)
            stroke_Gradient_Colors_Xml[0] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color0, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[1] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color1, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[2] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color2, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[3] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color3, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[4] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color4, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[5] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color5, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[6] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color6, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[7] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_ColorEnd, NOT_DEFINED_COLOR)
            stroke_Gradient_Angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Gradient_Angle, NOT_DEFINED_ANGLE))
            stroke_Gradient_OffCenter_X = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Gradient_OffCenter_X, DEFAULT_OFFCENTER_X), 1f)
            stroke_Gradient_OffCenter_Y = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Gradient_OffCenter_Y, DEFAULT_OFFCENTER_Y), 1f)
            stroke_DashSize = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_stroke_DashSize, DEFAULT_STROKE_DASH_SIZE), null)
            stroke_GapSize = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_stroke_GapSize, DEFAULT_STROKE_GAP_SIZE), null)
            stroke_CapType = CapType.values()[(typedArray.getInteger(R.styleable.AdvancedCardView_stroke_CapType, DEFAULT_STROKE_CAP_TYPE.ordinal))]
            // Get Shadow Data from XML
            // Outer Shadow 1
            shadows[0].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow0_Outer_Color, DEFAULT_SHADOW_COLOR)
            shadows[0].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Outer_Alpha, NOT_DEFINED_ALPHA))
            shadows[0].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Outer_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[0].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Outer_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[0].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Outer_Angle, NOT_DEFINED_ANGLE))
            // Outer Shadow 2
            shadows[1].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow1_Outer_Color, DEFAULT_SHADOW_COLOR)
            shadows[1].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Outer_Alpha, NOT_DEFINED_ALPHA))
            shadows[1].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Outer_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[1].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Outer_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[1].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Outer_Angle, NOT_DEFINED_ANGLE))
            // Inner Shadow 1
            shadows[2].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow0_Inner_Color, DEFAULT_SHADOW_COLOR)
            shadows[2].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Inner_Alpha, NOT_DEFINED_ALPHA))
            shadows[2].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Inner_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[2].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Inner_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[2].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Inner_Angle, NOT_DEFINED_ANGLE))
            // Inner Shadow 2
            shadows[3].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow1_Inner_Color, DEFAULT_SHADOW_COLOR)
            shadows[3].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Inner_Alpha, NOT_DEFINED_ALPHA))
            shadows[3].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Inner_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[3].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Inner_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[3].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Inner_Angle, NOT_DEFINED_ANGLE))
            //
            shadow_Outer_Area = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow_Outer_Area, NOT_DEFINED_SHADOW_OUTER_AREA), NOT_DEFINED_SHADOW_OUTER_AREA)
            //
            // Get Corner Radius from XML
            cornerRadius_ = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_TopLeft = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_TopLeft, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_TopRight = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_TopRight, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_BottomRight = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_BottomRight, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_BottomLeft = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_BottomLeft, NOT_DEFINED_CORNER_RADIUS)
            cornerType = CornerType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_corner_Type, DEFAULT_CORNER_TYPE.ordinal)]
            typedArray.recycle()
        }

        setWillNotDraw(false)
        clipToPadding = false
        initLayerTypes(background_Paint, stroke_Paint, shadows[0].paint, shadows[1].paint, shadows[2].paint, shadows[3].paint)
        initShadowOuterArea()
        initPadding()
        initBackgroundGradientColors()
        initStrokeGradientColors()

    }

    private fun initDebugFields() {
        DEBUG_paint_fill.style = Paint.Style.FILL
        DEBUG_paint_stroke.style = Paint.Style.STROKE
        DEBUG_paint_fill.color = DEBUG_fill_color
        DEBUG_paint_stroke.color = DEBUG_stroke_color
        DEBUG_paint_stroke.strokeWidth = DEBUG_stroke_width
        DEBUG_path.reset()
        DEBUG_path.addRect(0 + DEBUG_stroke_width / 2, 0 + DEBUG_stroke_width / 2, measuredWidth - DEBUG_stroke_width / 2, measuredHeight - DEBUG_stroke_width / 2, Path.Direction.CW)
    }

    private fun initShadowOuterArea() {
        if(shadow_Outer_Area == NOT_DEFINED_SHADOW_OUTER_AREA)
            if(haveOuterShadow() && shadow_Outer_Area != 0f)
                shadow_Outer_Area = DEFAULT_SHADOW_OUTER_AREA
    }

    private fun initPadding() {
        setPadding(getShadowOuterArea().toInt())
    }

    private fun initLayerTypes(vararg paints: Paint) {
        for(p in paints)
            if(android.os.Build.VERSION.SDK_INT < 28)
                setLayerType(View.LAYER_TYPE_SOFTWARE, p)
            else
                setLayerType(View.LAYER_TYPE_HARDWARE, p)
    }

    private fun initShadow(shadow: ShadowObject) {
        if(shadow.shadowType == ShadowType.Outer) {
            initShadowOuterPaint(shadow)
            initShadowOuterPath(shadow)
            initShadowOuterMask(shadow)
        } else if(shadow.shadowType == ShadowType.Inner) {
            initShadowInnerPaint(shadow)
            initShadowInnerPath(shadow)
            initShadowInnerMask(shadow)
        }
    }

    private fun initShadowOuterPaint(shadow: ShadowObject) {
        shadow.paint.style = Paint.Style.FILL
        shadow.paint.color = Color.rgb(128, 128, 128)
        shadow.paint.setShadowLayer(shadow.blur, getDx(shadow.distance, -shadow.angle), getDy(shadow.distance, -shadow.angle), assignColorAlpha(shadow.color, shadow.alpha))
    }

    private fun initShadowOuterPath(shadow: ShadowObject) {
        shadow.path.reset()
        addBackgroundRectF(shadow.path)
    }

    private fun initShadowOuterMask(shadow: ShadowObject) {
        shadow.mask.reset()
        addBoundaryRectF(shadow.mask)
//        addBackgroundRectF(shadow.mask)
        addBackgroundOuterShadowMaskRectF(shadow.mask)
        shadow.mask.fillType = Path.FillType.EVEN_ODD
    }

    private fun initShadowInnerPaint(shadow: ShadowObject) {
        shadow.paint.style = Paint.Style.FILL
        shadow.paint.color = Color.rgb(255, 255, 255)
        shadow.paint.setShadowLayer(shadow.blur, getDx(shadow.distance, -shadow.angle), getDy(shadow.distance, -shadow.angle), assignColorAlpha(shadow.color, shadow.alpha))
    }

    private fun initShadowInnerPath(shadow: ShadowObject) {
        shadow.path.reset()
        addInnerShadowExternalRectF(shadow.path, -shadow.blur * 4)
        addNoStrokeAreaRectF(shadow.path)
        shadow.path.fillType = Path.FillType.EVEN_ODD
    }

    private fun initShadowInnerMask(shadow: ShadowObject) {
        shadow.mask.reset()
        addNoStrokeAreaRectF(shadow.mask)
    }

    private fun initBackgroundPaint() {
        background_Paint.style = Paint.Style.FILL
        background_Paint.alpha = mapAlphaTo255(background_Alpha)
        background_Paint.color = background_Color
        when(background_ColorType) {
            ColorType.Gradient_Linear -> background_Paint.shader = getLinearShader(background_Gradient_Colors, background_Gradient_Angle)
            ColorType.Gradient_Radial -> background_Paint.shader = getRadialShader(background_Gradient_Colors, background_Gradient_OffCenter_X, background_Gradient_OffCenter_Y)
            ColorType.Gradient_Sweep -> background_Paint.shader = getSweepShader(background_Gradient_Colors, background_Gradient_Angle, background_Gradient_OffCenter_X, background_Gradient_OffCenter_Y)
            else -> background_Paint.shader = getLinearShader(background_Gradient_Colors, background_Gradient_Angle)
        }
    }

    private fun initStrokePaint() {
        stroke_Paint.style = Paint.Style.FILL
        stroke_Paint.alpha = mapAlphaTo255(stroke_Alpha)
        stroke_Paint.color = stroke_Color
        if(isDashed()) {
            stroke_Paint.style = Paint.Style.STROKE
            stroke_Paint.pathEffect = DashPathEffect(floatArrayOf(stroke_DashSize, stroke_GapSize), 0f)
            stroke_Paint.strokeCap = getStrokeCap(stroke_CapType)
            stroke_Paint.strokeWidth = getStrokeWidth()
        }
        when(stroke_ColorType) {
            ColorType.Gradient_Linear -> stroke_Paint.shader = getLinearShader(stroke_Gradient_Colors, stroke_Gradient_Angle)
            ColorType.Gradient_Sweep -> stroke_Paint.shader = getSweepShader(stroke_Gradient_Colors, stroke_Gradient_Angle, stroke_Gradient_OffCenter_X, stroke_Gradient_OffCenter_Y)
            else -> stroke_Paint.shader = getLinearShader(stroke_Gradient_Colors, stroke_Gradient_Angle)
        }
    }

    private fun initInnerShadowExternalPathRadii(inset: Float) {
        innerShadow_External_Path_Radii = getCornerRadii(getCornerRadius(), cornerRadius_TopLeft, cornerRadius_TopRight, cornerRadius_BottomRight, cornerRadius_BottomLeft, getStrokeWidth() + inset)
    }

    private fun initStrokeMaskRadii() {
        if(!stroke_Mask_Radii_Updated) {
            stroke_Mask_Radii = getCornerRadii(getCornerRadius(), cornerRadius_TopLeft, cornerRadius_TopRight, cornerRadius_BottomRight, cornerRadius_BottomLeft, getStrokeWidth())
            stroke_Mask_Radii_Updated = true
        }
    }

    private fun initStrokePathRadii() {
        if(!stroke_Path_Radii_Updated) {
            stroke_Path_Radii = getCornerRadii(getCornerRadius(), cornerRadius_TopLeft, cornerRadius_TopRight, cornerRadius_BottomRight, cornerRadius_BottomLeft, getStrokeWidth() / 2)
            stroke_Path_Radii_Updated = true
        }
    }

    private fun initBackgroundCornerRadii() {
        if(!background_Path_Radii_Updated) {
            background_Path_Radii = getCornerRadii(getCornerRadius(), cornerRadius_TopLeft, cornerRadius_TopRight, cornerRadius_BottomRight, cornerRadius_BottomLeft, 0f)
            background_Path_Radii_Updated = true
        }
    }

    private fun initBackgroundPath() {
        background_Path.reset()
        addBackgroundRectF(background_Path)
    }

    private fun initStrokePath() {
        stroke_Path.reset()
        addStrokePath(stroke_Path)
    }

    private fun initStrokeMask() {
        stroke_Mask.reset()
        addBackgroundRectF(stroke_Mask)
        addNoStrokeAreaRectF(stroke_Mask)
        stroke_Mask.fillType = Path.FillType.EVEN_ODD
    }

    private fun initNoStrokePath() {
        no_Stroke_Path.reset()
        addNoStrokeAreaRectF(no_Stroke_Path)
    }

    private fun initBackgroundGradientColors() {
        background_Gradient_Colors = getColorArray(background_Gradient_Colors_Xml, background_Alpha)
    }

    private fun initStrokeGradientColors() {
        stroke_Gradient_Colors = getColorArray(stroke_Gradient_Colors_Xml, stroke_Alpha)
    }

    private fun getCornerRadius(): Float {
        return when(cornerType) {
            CornerType.Custom -> cornerRadius_
            CornerType.Rectangular -> 0f
            CornerType.Circular -> min(getActualWidth(), getActualHeight()) / 2
            CornerType.Third -> min(getActualWidth(), getActualHeight()) / 3
            CornerType.Quarter -> min(getActualWidth(), getActualHeight()) / 4
        }
    }

    private fun getCornerRadii(radius: Float, topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float, inset: Float): FloatArray {
        val cornerRadius = if(radius == NOT_DEFINED_CORNER_RADIUS) DEFAULT_CORNER_RADIUS else dimenCheck(radius - inset, null)
        val cornerRadiusTopLeft = if(topLeft == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(topLeft - inset, null)
        val cornerRadiusTopRight = if(topRight == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(topRight - inset, null)
        val cornerRadiusBottomRight = if(bottomRight == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(bottomRight - inset, null)
        val cornerRadiusBottomLeft = if(bottomLeft == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(bottomLeft - inset, null)
        return floatArrayOf(
            cornerRadiusTopLeft,
            cornerRadiusTopLeft,
            cornerRadiusTopRight,
            cornerRadiusTopRight,
            cornerRadiusBottomRight,
            cornerRadiusBottomRight,
            cornerRadiusBottomLeft,
            cornerRadiusBottomLeft
        )
    }

    private fun getColorArray(colors: IntArray, alpha: Float): IntArray {
        var colorArray = ArrayList<Int>()
        for(c in colors)
            if(c != NOT_DEFINED_COLOR)
                colorArray.add(
                    assignColorAlpha(c, alpha)
                )
        if(colors.last() == NOT_DEFINED_COLOR && colorArray.size != 0)
            colorArray.add(colorArray.first())
        else if(colorArray.size == 0) {
            colorArray.add(DEFAULT_COLOR_STROKE)
            colorArray.add(DEFAULT_COLOR_STROKE)
        }
        return colorArray.toIntArray()
    }

    private fun assignColorAlpha(color: Int, alpha: Float): Int {
        return Color.argb(
            mapAlphaTo255(
                alpha * mapAlphaTo1(Color.alpha(color))
            ),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    private fun dimenCheck(dimen: Float, allowedDimen: Float?): Float {
        if(allowedDimen != null)
            if(dimen == allowedDimen)
                return dimen
        return if(dimen >= 0f)
            dimen
        else
            0f
    }

    private fun getRectF(width: Float, height: Float, inset: Float): RectF {
        return RectF(0f + inset, 0f + inset, width - inset, height - inset)
    }

    private fun addBackgroundRectF(path: Path) {
        path.addRoundRect(getRectF(measuredWidth.toFloat(), measuredHeight.toFloat(), getShadowOuterArea()), getBackgroundPathRadii(), Path.Direction.CW)
    }

    private fun addBackgroundOuterShadowMaskRectF(path: Path) {
        path.addRoundRect(getRectF(measuredWidth.toFloat(), measuredHeight.toFloat(), getShadowOuterArea() - SHADOW_OUTER_MASK_ERROR), getBackgroundPathRadii(), Path.Direction.CW)
    }

    private fun addStrokePath(path: Path) {
        path.addRoundRect(getRectF(measuredWidth.toFloat(), measuredHeight.toFloat(), getShadowOuterArea() + getStrokeWidth() / 2), getStrokePathRadii(), Path.Direction.CW)
    }

    private fun addNoStrokeAreaRectF(path: Path) {
        path.addRoundRect(getRectF(measuredWidth.toFloat(), measuredHeight.toFloat(), getShadowOuterArea() + getStrokeWidth()), getStrokeMaskRadii(), Path.Direction.CW)
    }

    private fun addBoundaryRectF(path: Path) {
        path.addRect(getRectF(measuredWidth.toFloat(), measuredHeight.toFloat(), 0f), Path.Direction.CW)
    }

    private fun addInnerShadowExternalRectF(path: Path, expand: Float) {
        path.addRoundRect(getRectF(measuredWidth.toFloat(), measuredHeight.toFloat(), getShadowOuterArea() + expand), getInnerShadowExternalPathRadii(expand), Path.Direction.CW)
    }

    private fun angleCheck(angle: Float): Float {
        return if(angle < 0)
            angleCheck(angle + 360)
        else
            angle % 360
    }

    private fun floatPercentCheck(alpha: Float): Float {
        return when {
            alpha < 0f -> 0f
            alpha > 1f -> 1f
            else -> alpha
        }
    }

    private fun mapAlphaTo255(alpha: Float): Int {
        return (floatPercentCheck(alpha) * 255).toInt()
    }

    private fun mapAlphaTo1(alpha: Int): Float {
        return alpha / 255f
    }

    private fun angleToRadians(angle: Float): Double {
        return Math.toRadians(angle.toDouble())
    }

    private fun getDx(distance: Float, angle: Float): Float {
        return (distance * sin(angleToRadians(angle))).toFloat()
    }

    private fun getDy(distance: Float, angle: Float): Float {
        return (distance * cos(angleToRadians(angle))).toFloat()
    }

    private fun getDiameter(x: Float, y: Float): Float {
        return sqrt(x.pow(2) + y.pow(2))
    }

    private fun boundaryCheck(value: Float, boundary: Float): Float {
        val absValue = abs(value)
        val sign = (value / absValue).toInt()
        return if(absValue >= boundary) sign * boundary
        else sign * absValue
    }

    // TODO( Create Rounded Sweep and Sudden Sweep Mode )
    private fun getSweepShader(colorArray: IntArray, angle: Float, offCenterX: Float, offCenterY: Float): SweepGradient {
        val sweepGradient = SweepGradient(getCenterX(offCenterX), getCenterY(offCenterY), colorArray, null)
        val matrix = Matrix()
        matrix.postRotate(angle - 90, getCenterX(offCenterX), getCenterY(offCenterY))
        sweepGradient.setLocalMatrix(matrix)
        return sweepGradient
    }

    private fun getRadialShader(colorArray: IntArray, offCenterX: Float, offCenterY: Float): RadialGradient {
        return RadialGradient(getCenterX(offCenterX), getCenterY(offCenterY), getShaderRadius(offCenterX, offCenterY), colorArray, null, Shader.TileMode.CLAMP)
    }

    private fun getCenterX(offCenter: Float): Float {
        val halfWidth = getActualWidth() / 2 + getShadowOuterArea()
        return halfWidth + offCenter * halfWidth
    }

    private fun getCenterY(offCenter: Float): Float {
        val halfHeight = getActualHeight() / 2 + getShadowOuterArea()
        return halfHeight + offCenter * halfHeight
    }

    private fun getShaderRadius(offCenterX: Float, offCenterY: Float): Float {
        val offCenter = getDiameter(offCenterX, offCenterY)
        val halfDiameter = getDiameter(getActualWidth(), getActualHeight()) / 2
        return halfDiameter + offCenter * halfDiameter
    }

    private fun getLinearShader(colorArray: IntArray, angle: Float): LinearGradient {
        val startPoint = getLinearGradientCircularStartPoint(angle)
        val endPoint = getLinearGradientCircularStartPoint(angle + 180)
        return LinearGradient(startPoint.first, startPoint.second, endPoint.first, endPoint.second, colorArray, null, Shader.TileMode.CLAMP)
    }

    private fun getLinearGradientCircularStartPoint(angle: Float): Pair<Float, Float> {
        val width = getActualWidth()
        val height = getActualHeight()
        val halfWidth = width / 2
        val halfHeight = height / 2
        val halfDiameter = getDiameter(width, height) / 2
        val dx = getDx(halfDiameter, angle)
        val dy = getDy(halfDiameter, angle)
        var x = halfWidth + boundaryCheck(dx, halfWidth)
        var y = halfHeight + boundaryCheck(-dy, halfHeight)
        // Make Coordinates of X and Y Circular Base on Corner Radius
        val pathRadii = getBackgroundPathRadii()
        val CTR = pathRadii[2]      // Corner Top Right
        val CBR = pathRadii[4]      // Corner Bottom Right
        val CBL = pathRadii[6]      // Corner Bottom Left
        val CTL = pathRadii[0]      // Corner Top Left
        var circularAngle: Float
        if(x >= width - CTR && y <= CTR) {
            // Top Right
            circularAngle = determineCornerCircularAngle(x - (width - CTR), y, CTR)
            // No Addition
            x = width - CTR + getDx(CTR, circularAngle)
            y = CTR - getDy(CTR, circularAngle)
        } else if(x >= width - CBR && y >= height - CBR) {
            // Bottom Right
            circularAngle = determineCornerCircularAngle(width - x, y - (height - CBR), CBR)
            circularAngle += 90
            x = width - CBR + getDx(CBR, circularAngle)
            y = height - CBR - getDy(CBR, circularAngle)
        } else if(x <= CBL && y >= height - CBL) {
            // Bottom Left
            circularAngle = determineCornerCircularAngle(CBL - x, height - y, CBL)    // Y Parameter is shorter version of "CBL - (y - (height - CBL))"
            circularAngle += 180
            x = CBL + getDx(CBL, circularAngle)
            y = height - CBL - getDy(CBL, circularAngle)
        } else if(x <= CTL && y <= CTL) {
            // Top Left
            circularAngle = determineCornerCircularAngle(x, CTL - y, CTL)
            circularAngle += 270
            x = CTL + getDx(CTL, circularAngle)
            y = CTL - getDy(CTL, circularAngle)
        }
        return Pair(x + getShadowOuterArea(), y + getShadowOuterArea())
    }

    private fun determineCornerCircularAngle(x: Float, y: Float, max: Float): Float {
        return (((abs(x) + abs(y)) / (2 * abs(max))) * 90)
    }

    private fun getBackgroundPathRadii(): FloatArray {
        initBackgroundCornerRadii()
        return background_Path_Radii
    }

    private fun getStrokeMaskRadii(): FloatArray {
        initStrokeMaskRadii()
        return stroke_Mask_Radii
    }

    private fun getStrokePathRadii(): FloatArray {
        initStrokePathRadii()
        return stroke_Path_Radii
    }

    private fun getInnerShadowExternalPathRadii(inset: Float): FloatArray {
        initInnerShadowExternalPathRadii(inset)
        return innerShadow_External_Path_Radii
    }

    private fun getStrokeCap(capType: CapType): Paint.Cap {
        return when(capType) {
            CapType.Butt -> Paint.Cap.BUTT
            CapType.Square -> Paint.Cap.SQUARE
            CapType.Round -> Paint.Cap.ROUND
        }
    }

    private fun cornerUpdated() {
        background_Path_Radii_Updated = false
        stroke_Path_Radii_Updated = false
        stroke_Mask_Radii_Updated = false
    }

    private fun getStrokePath(): Path {
        initStrokePath()
        return stroke_Path
    }

    private fun getStrokeMask(): Path {
        initStrokeMask()
        return stroke_Mask
    }

    private fun getStrokePaint(): Paint {
        initStrokePaint()
        return stroke_Paint
    }

    private fun getNoStrokePath(): Path {
        initNoStrokePath()
        return no_Stroke_Path
    }

    private fun isDashed(): Boolean {
        return stroke_Type == StrokeType.Dash
    }

    private fun getBackgroundPath(): Path {
        initBackgroundPath()
        return background_Path
    }

    private fun getBackgroundPaint(): Paint {
        initBackgroundPaint()
        return background_Paint
    }

    private fun getStrokeWidth(): Float {
        return if(canDrawStroke())
            stroke_Width
        else
            0f
    }

    private fun getShadowOuterArea(): Float {
        return if(shadow_Outer_Area == NOT_DEFINED_SHADOW_OUTER_AREA)
            0f
        else
            shadow_Outer_Area
    }

    private fun setShadowBlur(shadowIndex: Int, blur: Float) {
        shadows[shadowIndex].blur = dimenCheck(blur, null)
    }

    private fun setShadowDistance(shadowIndex: Int, distance: Float) {
        shadows[shadowIndex].distance = dimenCheck(distance, null)
    }

    private fun setShadowAlpha(shadowIndex: Int, alpha: Float) {
        shadows[shadowIndex].alpha = floatPercentCheck(alpha)
    }

    private fun setShadowColor(shadowIndex: Int, color: Int) {
        shadows[shadowIndex].color = color
    }

    private fun setShadowAngle(shadowIndex: Int, angle: Float) {
        shadows[shadowIndex].angle = angleCheck(angle)
    }

    private fun mapShadowIndex(shadowType: ShadowType, shadowIndex: Int): Int? {
        if(shadowType == ShadowType.Outer)
            return shadowIndex
        else if(shadowType == ShadowType.Inner)
            return shadowIndex + 2
        else
            return null
    }

    // Preparing Area
    // *
    // *
    // *

    // *
    // *
    // *
    // External Interface Area

    fun setShadow(shadowType: ShadowType, shadowIndex: Int, blur: Float, distance: Float, angle: Float, alpha: Float, color: Int) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null) {
            setShadowBlur(mappedShadowIndex, blur)
            setShadowDistance(mappedShadowIndex, distance)
            setShadowAlpha(mappedShadowIndex, alpha)
            setShadowColor(mappedShadowIndex, color)
            setShadowAngle(mappedShadowIndex, angle)
        } else
            return
    }

    fun setShadowBlur(shadowType: ShadowType, shadowIndex: Int, blur: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowBlur(mappedShadowIndex, blur)
        else
            return
    }

    fun setShadowDistance(shadowType: ShadowType, shadowIndex: Int, distance: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowDistance(mappedShadowIndex, distance)
        else
            return
    }

    fun setShadowAlpha(shadowType: ShadowType, shadowIndex: Int, alpha: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowAlpha(mappedShadowIndex, alpha)
        else
            return
    }

    fun setShadowColor(shadowType: ShadowType, shadowIndex: Int, color: Int) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowColor(mappedShadowIndex, color)
        else
            return
    }

    fun setShadowAngle(shadowType: ShadowType, shadowIndex: Int, angle: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowAngle(mappedShadowIndex, angle)
        else
            return
    }

    fun setShadowInnerBlur(shadowIndex: Int, blur: Float) {
        setShadowBlur(shadowIndex + 2, blur)
    }

    fun setShadowInnerDistance(shadowIndex: Int, distance: Float) {
        setShadowDistance(shadowIndex + 2, distance)
    }

    fun setShadowInnerAlpha(shadowIndex: Int, alpha: Float) {
        setShadowAlpha(shadowIndex + 2, alpha)
    }

    fun setShadowInnerColor(shadowIndex: Int, color: Int) {
        setShadowColor(shadowIndex + 2, color)
    }

    fun setShadowInnerAngle(shadowIndex: Int, angle: Float) {
        setShadowAngle(shadowIndex + 2, angle)
    }

    fun setShadowOuterBlur(shadowIndex: Int, blur: Float) {
        setShadowBlur(shadowIndex, blur)
    }

    fun setShadowOuterDistance(shadowIndex: Int, distance: Float) {
        setShadowDistance(shadowIndex, distance)
    }

    fun setShadowOuterAlpha(shadowIndex: Int, alpha: Float) {
        setShadowAlpha(shadowIndex, alpha)
    }

    fun setShadowOuterColor(shadowIndex: Int, color: Int) {
        setShadowColor(shadowIndex, color)
    }

    fun setShadowOuterAngle(shadowIndex: Int, angle: Float) {
        setShadowAngle(shadowIndex, angle)
    }

    fun setCorners(cornerRadius: Float, cornerRadius_TopLeft: Float, cornerRadius_TopRight: Float, cornerRadius_BottomRight: Float, cornerRadius_BottomLeft: Float) {
        this.cornerRadius_ = cornerRadius
        this.cornerRadius_TopLeft = cornerRadius_TopLeft
        this.cornerRadius_TopRight = cornerRadius_TopRight
        this.cornerRadius_BottomRight = cornerRadius_BottomRight
        this.cornerRadius_BottomLeft = cornerRadius_BottomLeft
    }

    fun setCorners(cornerRadius_TopLeft: Float, cornerRadius_TopRight: Float, cornerRadius_BottomRight: Float, cornerRadius_BottomLeft: Float) {
        this.cornerRadius_TopLeft = cornerRadius_TopLeft
        this.cornerRadius_TopRight = cornerRadius_TopRight
        this.cornerRadius_BottomRight = cornerRadius_BottomRight
        this.cornerRadius_BottomLeft = cornerRadius_BottomLeft
    }

    fun haveInnerShadow(): Boolean {
        return shadows[2].blur != NOT_DEFINED_DIMEN || shadows[3].blur != NOT_DEFINED_DIMEN
    }

    fun haveOuterShadow(): Boolean {
        return shadows[0].blur != NOT_DEFINED_DIMEN || shadows[1].blur != NOT_DEFINED_DIMEN
    }

    fun haveAnyShadow(): Boolean {
        return haveInnerShadow() || haveOuterShadow()
    }

    fun getActualWidth(): Float {
        return measuredWidth - getShadowOuterArea() * 2
    }

    fun getActualHeight(): Float {
        return measuredHeight - getShadowOuterArea() * 2
    }

    // External Interface Area
    // *
    // *
    // *

    // *
    // *
    // *
    // Drawing Area

    private fun canDrawBackground(): Boolean {
        return background_Type == BackgroundType.Fill || background_Type == BackgroundType.Fill_Stroke
    }

    private fun canDrawStroke(): Boolean {
        return background_Type == BackgroundType.Stroke || background_Type == BackgroundType.Fill_Stroke
    }

    private fun canDrawShadow(shadow: ShadowObject): Boolean {
        if(shadow.shadowType == ShadowType.Outer && getShadowOuterArea() == 0f)
            return false
        return shadow.blur != NOT_DEFINED_DIMEN && (canDrawStroke() || canDrawBackground())
    }

    private fun drawBackground(canvas: Canvas?) {
        if(canDrawBackground()) {
            canvas?.drawPath(getBackgroundPath(), getBackgroundPaint())
        }
    }

    private fun drawStroke(canvas: Canvas?) {
        if(canDrawStroke()) {
            if(isDashed()) {
                canvas?.save()
                canvas?.clipPath(getStrokeMask())
                canvas?.drawPath(getStrokePath(), getStrokePaint())
                canvas?.restore()
            } else {
                canvas?.drawPath(getStrokeMask(), getStrokePaint())
            }
        }
    }

    private fun drawShadow(canvas: Canvas?, shadow: ShadowObject) {
        canvas?.save()
        canvas?.clipPath(shadow.mask)
        canvas?.drawPath(shadow.path, shadow.paint)
        canvas?.restore()
    }

    private fun drawShadows(canvas: Canvas?) {
        for(shadow in shadows)
            if(canDrawShadow(shadow)) {
                initShadow(shadow)
                drawShadow(canvas, shadow)
            }
    }

    private fun drawDebugMode(canvas: Canvas?) {
        if(DEBUG_MODE) {
            initDebugFields()
            canvas?.drawPath(DEBUG_path, DEBUG_paint_stroke)
            DEBUG_path.reset()
            val startPoint = getLinearGradientCircularStartPoint(background_Gradient_Angle)
            val endPoint = getLinearGradientCircularStartPoint(background_Gradient_Angle + 180)
            canvas?.drawLine(startPoint.first, startPoint.second, endPoint.first, endPoint.second, DEBUG_paint_stroke)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawStroke(canvas)
        drawShadows(canvas)
        drawDebugMode(canvas)
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        canvas.clipPath(getNoStrokePath())
        return super.drawChild(canvas, child, drawingTime)
    }
}