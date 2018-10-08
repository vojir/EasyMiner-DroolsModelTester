<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ar="http://keg.vse.cz/lm/AssociationRules/v1.0" exclude-result-prefixes="ar" version="1.0">
    
    <xsl:output encoding="UTF-8" method="text" indent="no" xml:space="default" />
    
    <xsl:template match="/">
        <xsl:apply-templates select="/ar:AssociationRules/ar:AssociationRule" />
    </xsl:template>
    
    <xsl:template match="ar:AssociationRule">
        <xsl:variable name="antecendentAttributesCount" select="count(./ar:Antecedent//ar:Attribute)" />
        rule "rule_<xsl:value-of select="@id"/>"
        salience 3
        no-loop true
        when
        $ar:DrlAR(id=="")<xsl:if test="$antecendentAttributesCount&gt;0"> and (<xsl:apply-templates select="./ar:Antecedent" />)</xsl:if>
        then
        DrlAR $thisAR=new DrlAR("rule_<xsl:value-of select="@id"/>"
        ,<xsl:value-of select="$antecendentAttributesCount" />
        <xsl:choose>
            <xsl:when test="./ar:Rating">
                ,<xsl:value-of select="./ar:Rating/@confidence"/>
                ,<xsl:value-of select="./ar:Rating/@support"/>
                ,0.0
                ,"<xsl:value-of select="./ar:Consequent//ar:Attribute/ar:Category/ar:Data/ar:Value" />");
            </xsl:when>
            <xsl:otherwise>
                ,<xsl:value-of select="(./ar:FourFtTable/@a div (./ar:FourFtTable/@a + ./ar:FourFtTable/@b))"/>
                ,<xsl:value-of select="(./ar:FourFtTable/@a div (./ar:FourFtTable/@a + ./ar:FourFtTable/@b + ./ar:FourFtTable/@c + ./ar:FourFtTable/@d))"/>
                ,<xsl:value-of select="((./ar:FourFtTable/@a + ./ar:FourFtTable/@c) div (./ar:FourFtTable/@a + ./ar:FourFtTable/@b + ./ar:FourFtTable/@c + ./ar:FourFtTable/@d))"/>
                ,"<xsl:value-of select="./ar:Consequent//ar:Attribute/ar:Category/ar:Data/ar:Value" />");        
            </xsl:otherwise>
        </xsl:choose>
        if (isBetterAR($ar,$thisAR)){
        $ar.updateFromAR($thisAR);
        update($ar);   
        }   
        end
        rule "rule_<xsl:value-of select="@id"/>_consequent"
        salience 2
        when
        $ar:DrlAR(id=="rule_<xsl:value-of select="@id"/>") and
        (<xsl:apply-templates select="./ar:Consequent" />)
        then
        $ar.setCheckedOk(true);
        $ar.setId("");
        update($ar);          
        end
    </xsl:template>
    
    <xsl:template match="ar:Cedent|ar:Antecedent|ar:Consequent" >
        <xsl:if test="@connective='Negation'">
            <!-- tady by to možná chtělo nějak  -->
            not
        </xsl:if>
        <xsl:variable name="connective" select="@connective" />

        <xsl:if test="count(./*)&gt;1">
            (
        </xsl:if>
        <xsl:for-each select="ar:Cedent|ar:Attribute">
            <xsl:apply-templates select="." />
            <xsl:if test="position() != last()">
                <xsl:choose>
                    <xsl:when test="$connective='Disjunction'"> or </xsl:when>
                    <xsl:otherwise> and </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="count(./*)&gt;1">
            )
        </xsl:if>
    </xsl:template>

    <xsl:template match="ar:Attribute" xml:space="preserve" >
        DrlObj (name == "<xsl:value-of select="./ar:Column/text()"/>",
        <xsl:apply-templates select="ar:Category" />
        )
    </xsl:template>
    
    <xsl:template match="ar:Category">
        <xsl:if test="position()>1"> || </xsl:if>
        <xsl:apply-templates select="ar:Data" />
    </xsl:template>
    
    <xsl:template match="ar:Data">
        <xsl:choose>
            <xsl:when test="count(./*)>1">(<xsl:apply-templates select="./*" />)</xsl:when>
            <xsl:otherwise><xsl:apply-templates select="./*" /></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="ar:Data/ar:Interval">
        <xsl:if test="position()>1"> || </xsl:if>
        <xsl:choose>
            <xsl:when test="./@closure='closedClosed'">(numVal &gt;= <xsl:value-of select="@leftMargin"/> &amp;&amp; numVal &lt;= <xsl:value-of select="@rightMargin"/>)</xsl:when>
            <xsl:when test="./@closure='openClosed'">  (numVal &gt; <xsl:value-of select="@leftMargin"/>  &amp;&amp; numVal &lt;= <xsl:value-of select="@rightMargin"/>)</xsl:when>
            <xsl:when test="./@closure='closedOpen'">  (numVal &gt;= <xsl:value-of select="@leftMargin"/> &amp;&amp; numVal &lt; <xsl:value-of select="@rightMargin"/>)</xsl:when>
            <xsl:when test="./@closure='openOpen'">    (numVal &gt; <xsl:value-of select="@leftMargin"/>  &amp;&amp; numVal &lt; <xsl:value-of select="@rightMargin"/>)</xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="ar:Data/ar:Value" xml:space="preserve"><xsl:if test="position()>1"> || </xsl:if> value == "<xsl:value-of select="text()"/>"</xsl:template>
</xsl:stylesheet>